package org.exquisite.communication.protocol;

import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import org.exquisite.communication.messages.ClientMessages;
import org.exquisite.communication.messages.ServerMessages;
import org.exquisite.data.*;
import org.exquisite.datamodel.*;
import org.exquisite.datamodel.ExquisiteEnums.ContentTypes;
import org.exquisite.datamodel.ExquisiteEnums.StatusCodes;
import org.exquisite.datamodel.serialisation.*;
import org.exquisite.diagnosis.EngineFactory;
import org.exquisite.diagnosis.IDiagnosisEngine;
import org.exquisite.diagnosis.formulaquerying.FormulaQuerying;
import org.exquisite.diagnosis.models.Diagnosis;
import org.exquisite.diagnosis.parallelsearch.ParallelSolver;
import org.exquisite.fragmentation.IFragmentExtractor;
import org.exquisite.fragmentation.OneCellFragmentExtractor;
import org.exquisite.fragmentation.genetic.GeneticFragmentExtractor;
import org.exquisite.threading.INotifyingThreadListener;
import org.exquisite.threading.NotifyingThread;
import org.exquisite.tools.Utilities;
import org.exquisite.xml.XMLParser;
import org.w3c.dom.DOMException;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * <tt>ServerProtocol</tt> manages the conversation between the client and server. <tt>Server</tt> routes requests from the client to
 * <tt>ServerProtocol</tt> which in turn generates responses that <tt>Server</tt> sends back to the client.
 * A static main method has been included to test/demonstrate the flow of data between client and server.<p>
 * <p>
 * The conversation can be summarized as follows:
 * <ol>
 * <li>The client issues a <strong>POST_MODEL</strong> message to prepare <tt>ServerProtocol</tt> for the incoming data.</li>
 * <li><tt>ServerProtocol</tt> responds <strong>OK</strong> or <strong>ERROR</strong>.</li>
 * <li>If Client gets back an <strong>OK</strong> then the client should send off its model serialized as XML.</li>
 * <li><tt>ServerProtocol</tt> receives the XML and parses it. If the parse was successful it responds with <strong>OK</strong>, otherwise <strong>ERROR</strong>.</li>
 * <li>Client can then issue a <strong>REQUEST_DIAGNOSIS</strong> message - which would start a tests.diagnosis.</li>
 * <li>Client then polls the server repeatedly with a <strong>REQUEST_DIAGNOSIS_RESULT</strong> message.</li>
 * <li>Server responds with status code of <strong>BUSY</strong> while the tests.diagnosis is in progress, then <strong>DIAGNOSIS_READY</strong> when tests.diagnosis has finished.</li>
 * <li>When Client receives <strong>DIAGNOSIS_READY</strong> response, it sends a <strong>REQUEST_DIAGNOSIS_RESULT</strong> message.</li>
 * <li>Server sends the results of the tests.diagnosis.</li>
 * </ol>
 * The client can also send a <strong>CANCEL_REQUEST</strong> message which causes the server to stop what it
 * is doing and revert to its IDLE state - this is however subject to how the solver is implemented.<p>
 *
 * @author David
 * @see org.exquisite.communication.Server
 * @see org.exquisite.threading.INotifyingThreadListener
 */
public class ServerProtocol implements INotifyingThreadListener {
    /**
     * Ready for a new task...
     */
    private final int STATE_IDLE = 0;
    /**
     * About to process a new model when the client sends it.
     */
    private final int STATE_RECEIVING_MODEL = 1;
    /**
     * Currently stopping the tests.diagnosis thread.
     */
    private final int STATE_CANCELLING_DIAGNOSIS = 2;
    /**
     * Currently running a tests.diagnosis calculation.
     */
    private final int STATE_RUNNING_DIAGNOSIS = 3;
    /**
     * A tests.diagnosis calculation has finished and can be sent back to the client.
     */
    private final int STATE_DIAGNOSIS_READY = 4;
    /**
     * About to process list of cellnames when the client sends them.
     */
    private final int STATE_RECEIVING_CELLNAMES = 5;
    /**
     * Session data instance, this is updated every time an ExquisiteAppXML
     * object is sent from the client.
     */
    private ExquisiteSession sessionData = null;
    /**
     * The results of the last run of the tests.diagnosis engine.
     */
    private List<Diagnosis<Constraint>> diagnoses = null;
    /**
     * To keep internal state of this protocol. The initial default state is set to IDLE.
     */
    private int state = STATE_IDLE;

    /**
     * Text representation of internal server state, more readable for debugging etc.
     */
    private String[] stateLookup = {
            "Idle.",
            "Ready to receive a model.",
            "Cancelling tests.diagnosis.",
            "Running tests.diagnosis.",
            "Diagnosis ready.",
            "Ready to receive cellnames."
    };

    /**
     * The thread the tests.diagnosis engine instance will run in. This is a notifying thread which means the thread will
     * broadcast an event to any listeners when the object running in this thread has finished its work.
     *
     * @see org.exquisite.threading.NotifyingThread
     */
    private NotifyingThread diagnosisEngineThread;

    /**
     * When <tt>ServerProtocol</tt> is instantiated. sessionData is also instantiated in order to use initial defaults in config.
     * sessionData will be overwritten when Client later sends <tt>ExquisiteAppXML</tt>.
     *
     * @see org.exquisite.datamodel.ExquisiteSession
     * @see org.exquisite.datamodel.ExquisiteAppXML
     */
    public ServerProtocol() {
        this.sessionData = new ExquisiteSession();
    }

    /**
     * Runs some tests to demonstrate how ServerProtocol works.
     *
     * @param args
     */
    public static void main(String[] args) {
        System.out.println("--- Start Server Protocol example.");
        ServerProtocol protocol = new ServerProtocol();
        protocol.sessionData.config.showServerDebugMessages = true;
        runExampleConversation(protocol);

        //repeated to make sure it still can run after an initial pass.
        System.out.println(" --- and repeat --- ");
        runExampleConversation(protocol);
        System.out.println("--- Finished Server Protocol example.");

        //Exit.
        protocol.processClientInput(ClientMessages.SHUT_DOWN);
    }

    /**
     * Test the protocol - demonstrates an example conversation between
     * client and server.
     *
     * @param protocol the ServerProtocol instance to test.
     */
    private static void runExampleConversation(ServerProtocol protocol) {
        ExquisiteMessage msg;

        //Request to send the data from the client
        String clientMessage = ClientMessages.POST_MODEL;
        msg = protocol.processClientInput(clientMessage);

        //Sending the data from the client...
        clientMessage = ExampleTestData.SMALL_TEST;
        msg = protocol.processClientInput(clientMessage);

        //Request the service to perform a tests.diagnosis calculation.
        clientMessage = ClientMessages.REQUEST_DIAGNOSIS;
        msg = protocol.processClientInput(clientMessage);

        //Request tests.diagnosis result NOTE: should return BUSY because
        //tests.diagnosis calculation has only just started...
        clientMessage = ClientMessages.REQUEST_DIAGNOSIS_RESULT;
        msg = protocol.processClientInput(clientMessage);

        //Request to cancel the tests.diagnosis calculation to simulate user changing their mind.
        clientMessage = ClientMessages.CANCEL_REQUEST;
        msg = protocol.processClientInput(clientMessage);

        //Request a tests.diagnosis - simulate to start tests.diagnosis again after cancellation.
        //This results in a BUSY response because the thread needs time to clean up.
        clientMessage = ClientMessages.REQUEST_DIAGNOSIS;
        msg = protocol.processClientInput(clientMessage);

        //Deliberately sleep to ensure tests.diagnosis thread is complete when we try again
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
        }

        //send tests.diagnosis result back to client
        clientMessage = ClientMessages.REQUEST_DIAGNOSIS_RESULT;
        msg = protocol.processClientInput(clientMessage);
        System.out.println("Protocol response: " + msg.items.get(0).content);
    }

    /**
     * Manages the requests coming from and responses going back to the client.
     *
     * @param message the message received from the client.
     * @return <tt>ExquisiteMessage</tt> object containing the response status code and message items to send back to the client.
     * @see org.exquisite.datamodel.ExquisiteMessage
     * @see org.exquisite.communication.Server
     */
    public ExquisiteMessage processClientInput(String message) {
        if (this.sessionData.config.showServerDebugMessages) {
            System.out.println(
                    "processClientInput, client message = (" + message + ") server state at start = " + stateLookup[this.state]);
        }

        /**
         * Default server response is ERROR
         */
        ExquisiteMessage response = new ExquisiteMessage();
        response.host = "Exquisite-Service";
        response.status = StatusCodes.ERROR;

        /**
         * The client messages SHUT_DOWN, DISCONNECT and CANCEL_REQUEST
         * are not dependent on the state of the server, hence why they
         * are outside of the state switch statement.
         */
        //-- Client has issued a SHUT_DOWN request
        if (ClientMessages.SHUT_DOWN.equalsIgnoreCase(message)) {
            if (this.sessionData.config.showServerDebugMessages) {
                System.out.println("Shutting down.");
            }
            System.exit(0);
        }
        //------------------------------------------------------------

        //-- Client has issued a DISCONNECT request
        if (ClientMessages.DISCONNECT.equalsIgnoreCase(message)) {
            response.status = StatusCodes.OK;
            return response;
        }
        //------------------------------------------------------------

        //-- Client has issued a CANCEL request
        if (ClientMessages.CANCEL_REQUEST.equalsIgnoreCase(message)) {
            //stop whatever is currently happening
            //change to STATE_CANCELLING_DIAGNOSIS
            //return RESPONSE_OK - or error if not able to cancel?

            if (state != STATE_IDLE) {
                ParallelSolver.STOP_SEARCH = true;
                diagnosisEngineThread.interrupt();
                state = STATE_CANCELLING_DIAGNOSIS;
            }
            response.status = StatusCodes.OK;
            return response;
        }
        //------------------------------------------------------------

        //Handling client messages that have dependencies on the state of the protocol.
        switch (state) {
            //when idle, accepted messages are to receive a model from the client or
            //request a tests.diagnosis.
            case STATE_IDLE:
                //-- Client wants to post the AppXML data to the server.
                if (ClientMessages.POST_MODEL.equalsIgnoreCase(message)) {
                    response.status = StatusCodes.OK;
                    state = STATE_RECEIVING_MODEL;
                }
                //------------------------------------------------------------

                //-- Client wants to start a tests.diagnosis
                if (ClientMessages.REQUEST_DIAGNOSIS.equalsIgnoreCase(message)) {
                    if (this.sessionData != null) {

                        diagnosisEngineThread = initialiseDagEngineThread();
                    } else {
                        response.status = StatusCodes.ERROR;
                        return response;
                    }
                    diagnosisEngineThread.start();
                    response.status = StatusCodes.OK;
                    state = STATE_RUNNING_DIAGNOSIS;
                }
                //------------------------------------------------------------

                //-- If the client has not sent the AppXML but requests a tests.diagnosis then send an error message back.
                if (ClientMessages.REQUEST_DIAGNOSIS_RESULT.equalsIgnoreCase(message)) {
                    response.status = StatusCodes.OK;
                    ExquisiteMessageItem item = new ExquisiteMessageItem();
                    item.type = ContentTypes.Error;
                    item.content = ServerMessages.RESPONSE_DIAGNOSIS_NOT_RUN;
                    response.addItem(item);
                }
                //------------------------------------------------------------

                //-- Client wants to get formulas to query the user for correctness
                if (ClientMessages.REQUEST_FORMULAS_TO_QUERY.equalsIgnoreCase(message)) {
                    if (diagnoses == null) {
                        response.status = StatusCodes.OK;
                        ExquisiteMessageItem item = new ExquisiteMessageItem();
                        item.type = ContentTypes.Error;
                        item.content = ServerMessages.RESPONSE_DIAGNOSIS_NOT_RUN;
                        response.addItem(item);
                    } else {
                        FormulaQuerying<Constraint> formulaQuerying = new FormulaQuerying(sessionData);
                        List<String> cellnames = formulaQuerying.determineFormulasToQuery(diagnoses);
                        ExquisiteCellNames exquisiteCellNames = new ExquisiteCellNames();
                        exquisiteCellNames.cellNames.addAll(cellnames);
                        response.status = StatusCodes.OK;
                        ExquisiteMessageItem item = new ExquisiteMessageItem();
                        item.type = ContentTypes.Cellnames;
                        item.content = exquisiteCellNames.toXML();
                        response.addItem(item);
                    }
                }
                //------------------------------------------------------------

                //-- Client wants to get fragmentation of the spreadsheet
                if (ClientMessages.REQUEST_FRAGMENTATION.equalsIgnoreCase(message)) {
                    response.status = StatusCodes.OK;
                    ExquisiteMessageItem item = new ExquisiteMessageItem();

                    // TODO: Do work in another thread.
                    //IFragmentExtractor extractor = new OneCellFragmentExtractor(this.sessionData.appXML);
                    //IFragmentExtractor extractor = new MergingFragmentExtractor(this.sessionData.appXML);
                    IFragmentExtractor extractor = new GeneticFragmentExtractor(this.sessionData.appXML);
                    System.out.println("Calculating fragmentation...");
                    long start = System.nanoTime();
                    List<Fragment> fragments = extractor.calculateFragmentation();

                    ExquisiteFragmentation fragmentation = new ExquisiteFragmentation(fragments);
                    long end = System.nanoTime();
                    System.out.println("Finished after " + (end - start) / 1000000 + "ms.");

                    item.type = ContentTypes.Fragments;
                    item.content = fragmentation.toXML();
                    response.addItem(item);
                }
                //------------------------------------------------------------

                //-- Client wants to get a fragment of the selected cells
                if (ClientMessages.REQUEST_FRAGMENT_OF_CELLS.equalsIgnoreCase(message)) {
                    response.status = StatusCodes.OK;
                    state = STATE_RECEIVING_CELLNAMES;
                }
                //------------------------------------------------------------
                break;
            case STATE_RECEIVING_MODEL:
                //parse model data sent from client.
                if (!ClientMessages.REQUEST_DIAGNOSIS_RESULT.equalsIgnoreCase(message)) {
                    try {
                        this.sessionData = new ExquisiteSession();
                        XMLParser xmlParser = new XMLParser();
                        xmlParser.parse(message);
                        ExquisiteAppXML appXML = StringPreprocessor
                                .processForImport(xmlParser.getExquisiteAppXML(), this.sessionData.config);

						/*
                        XMLWriter writer = new XMLWriter();
						try {
							FileUtilities.writeToFile(writer.writeXML(appXML), "C:\\exquisite testing\\test.xml", false);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						*/

                        this.sessionData.appXML = appXML;
                        ConstraintsFactory conFactory = new ConstraintsFactory(sessionData);
                        VariablesFactory varFactory = new VariablesFactory(
                                new Hashtable<String, IntegerExpressionVariable>());
                        DiagnosisModelLoader loader = new DiagnosisModelLoader(sessionData, varFactory, conFactory);
                        loader.loadDiagnosisModelFromXML();
                        this.sessionData.config
                                .updateFromUserSettings(xmlParser.getExquisiteAppXML().getUserSettings());
                        response.status = StatusCodes.OK;
                    } catch (DOMException e) {
                        response.status = StatusCodes.ERROR;
                        e.printStackTrace();
                    }
                    //now ready to run the tests.diagnosis when requested by client.
                    state = STATE_IDLE;
                }
                break;
            case STATE_RECEIVING_CELLNAMES:
                try {
                    CellNamesParser cellNamesParser = new CellNamesParser();
                    cellNamesParser.parse(message);
                    ExquisiteCellNames cellNames = cellNamesParser.getCellNames();

                    IFragmentExtractor extractor = new OneCellFragmentExtractor(this.sessionData.appXML);
                    Fragment fragment = extractor.buildFragment(cellNames.cellNames);
                    extractor.printComplexity(fragment);
                    ExquisiteFragmentation fragmentation = new ExquisiteFragmentation();
                    fragmentation.Fragments.add(fragment);

                    ExquisiteMessageItem item = new ExquisiteMessageItem();
                    response.status = StatusCodes.OK;
                    item.type = ContentTypes.Fragments;
                    item.content = fragmentation.toXML();
                    response.addItem(item);
                } catch (DOMException e) {
                    response.status = StatusCodes.ERROR;
                    e.printStackTrace();
                }
                //now ready to run the tests.diagnosis when requested by client.
                state = STATE_IDLE;
                break;
            //Return a BUSY response while the protocol is
            //running or canceling a tests.diagnosis calculation
            case STATE_CANCELLING_DIAGNOSIS:
            case STATE_RUNNING_DIAGNOSIS:
                response.status = StatusCodes.BUSY;
                break;
            //------------------------------------------------------------
            //Return the tests.diagnosis result to the client if requested.
            case STATE_DIAGNOSIS_READY:
                if (ClientMessages.REQUEST_DIAGNOSIS_RESULT.equalsIgnoreCase(message)) {
                    if (this.sessionData.config.showServerDebugMessages) {
                        for (int i = 0; i < diagnoses.size(); i++) {
                            System.out.println("-- Diagnosis #" + i);
                            System.out.println("    " + Utilities
                                    .printConstraintList(new ArrayList<Constraint>(diagnoses.get(i).getElements()),
                                            this.sessionData.diagnosisModel));
                            System.out.println("--");
                        }
                        System.out.println("this.diagnoses.size = " + this.diagnoses.size());
                    }
                    response.status = StatusCodes.OK;

                    ExquisiteMessageItem item = new ExquisiteMessageItem();
                    item.type = ContentTypes.Diagnoses;

                    //Add diagnoses to ExquisiteDiagnosisResults object. This class contains
                    //the correct annotations for serializing the diagnoses into an XML format
                    //understandable by the Excel-AddIn client.
                    ExquisiteDiagnosisResults diagResults = new ExquisiteDiagnosisResults();
                    for (int i = 0; i < diagnoses.size(); i++) {
                        Diagnosis<Constraint> diagnosis = diagnoses.get(i);
                        ExquisiteDiagnosisResult result = new ExquisiteDiagnosisResult();
                        // TODO: Add real rank (same rank for diagnoses with same fault probability)
                        result.rank = "" + (i + 1);
                        for (Constraint constraint : diagnosis.getElements()) {
                            result.candidates.add(this.sessionData.diagnosisModel.getConstraintName(constraint));
                        }
                        diagResults.results.add(result);
                    }
                    item.content = diagResults.toXML();
                    response.addItem(item);
                    state = STATE_IDLE;
                }
                break;
        }
        //return the response.
        return response;
    }


    // -- Tests -------------------------------------------------------

    /**
     * Prepares an instance with <tt>IDiagnosisEngine</tt> interface implementation
     * to be run in a separate thread.<p>
     * <p>
     * <ol>
     * <li>Instantiates the tests.diagnosis engine that is to perform the tests.diagnosis; e.g. <tt>HSDagBuilder</tt> or <tt>ParallelHSDagBuilder</tt> etc.</li>
     * <li>Instantiates a <tt>DiagnosisRunner</tt> object and passes the tests.diagnosis engine to it.</li>
     * <li>Makes a <tt>NotifyingThread</tt> instance and passes the <tt>DiagnosisRunner</tt> object to that.</li>
     * </ol>
     * When the tests.diagnosis engine's work has finished, the notifying thread will
     * broadcast an event. This ServerProtocol instance will be listening for this event, and will then update diagnoses results with the results from this last run.
     *
     * @return <tt>NotifyingThread</tt> instance that the IDiagnosisEngine will run in.
     */
    private NotifyingThread initialiseDagEngineThread() {
        IDiagnosisEngine<Constraint> engine = EngineFactory.makeDAGEngineStandardQx(this.sessionData);
        //IDiagnosisEngine engine = EngineFactory.makeParaDagEngineStandardQx(this.sessionData, 4);
//		IDiagnosisEngine engine = EngineFactory.makeHeuristicSearchEngine(sessionData,4);

        //instantiate a runner to wrap the tests.diagnosis engine.
        DiagnosisRunner runner = new DiagnosisRunner(engine);

        //instantiate a thread for the tests.diagnosis runner to be run in.
        NotifyingThread thread = new NotifyingThread(runner);
        //thread will then notify ServerProtocol when the tests.diagnosis engine in the runner object has finished.
        thread.addListener(this);
        thread.setDaemon(true);
        return thread;
    }

    /**
     * An event handler to react when the diagnosisEngine thread has finished.<p>
     * <p>
     * If the tests.diagnosis engine ran to completion then the diagnoses list is updated
     * and the state is changed to DIAGNOSIS_READY.
     * <p>
     * If the thread was interrupted (e.g. the client sent a <strong>CANCEL_DIAGNOSIS</strong> request) then the state is
     * reverted to IDLE.<p>
     *
     * @param thread the thread that sent the notification event.
     */
    @Override
    public void notifyOfThreadComplete(NotifyingThread thread) {
        if (thread.isInterrupted()) {
            if (this.sessionData.config.showServerDebugMessages) {
                System.out.println("Diagnosis was cancelled, shifting to idle state.");
            }
            state = STATE_IDLE;
        } else { //therefore if tests.diagnosis is not null then the calculation must have finished.
            DiagnosisRunner runner = ((DiagnosisRunner) thread.getRunner());
            System.out.println("Found " + runner.diagnoses
                    .size() + " diagnoses in " + (runner.endTime - runner.startTime) + " ms, shifting to tests.diagnosis ready state.");
            List<Diagnosis<Constraint>> runnerDiagnoses = runner.diagnoses;

            if (runnerDiagnoses.size() > 0) {
                Diagnosis<Constraint> d = runnerDiagnoses.get(0);
                System.out.println("The conflicts: " + d.getElements().size());
                System.out.println(d.getElements());
            }


            diagnoses = new ArrayList<Diagnosis<Constraint>>(runnerDiagnoses);
            state = STATE_DIAGNOSIS_READY;
        }
    }
}