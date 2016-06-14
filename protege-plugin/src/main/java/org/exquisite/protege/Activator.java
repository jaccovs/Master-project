package org.exquisite.protege;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.semanticweb.owlapi.io.ToStringRenderer;
import org.semanticweb.owlapi.manchestersyntax.renderer.ManchesterOWLSyntaxOWLObjectRendererImpl;
import org.semanticweb.owlapi.util.SimpleRenderer;

public class Activator implements BundleActivator {

    static {
        System.setProperty("org.slf4j.simplelogger.defaultlog", "warn");
    }

    private Logger logger = Logger.getLogger(Activator.class.getName());

    public void start(BundleContext bundleContext) throws Exception {
        ToStringRenderer.getInstance().setRenderer(new ManchesterOWLSyntaxOWLObjectRendererImpl());
        logger.debug("bundle started");
    }

    public void stop(BundleContext bundleContext) throws Exception {
        ToStringRenderer.getInstance().setRenderer(new SimpleRenderer());

    }

}
