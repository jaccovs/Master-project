# Knowledge-base debugger (KBD)

The KBD library implements a number of model-based diagnosis algorithms adapted to diagnosis of knowledge bases and logic programs. 
The debugger allows its users to specify:
* *requirements to the knowledge base*, e.g. consistency, coherency, etc.;
* *test cases*, which are formulas in the knowledge representation language used to encode the knowledge base satisfying the defined requirements;
* *background knowledge*, which is a part of the knowledge base, which is known to be correct.

Given a knowledge base violating some of the predefined requirements or test cases the library computes a set of: 
* *conflicts*, which are irreducible subsets of KB formulas causing violation of requirements/test cases; 
* *diagnoses*, which are minimal subsets of KB that should be changed in order to satisfy all the requirements/test cases. 

In the standard implementation conflicts are computed using Junker's QuickXPlain algorithm whereas diagnoses by a corrected version of the HS-Tree algorithm presented in Reiter's "A Theory of Diagnosis from First Principles". However, we constantly improve the performance of the library by developing new algorithms, such as MergeXPlain, as well as novel ways of their applications (see Wiki for more details).

In general case the library can return many diagnoses as there may exists multiple ways to repair a KB. Therefore we developed an interactive debugging algorithm that allows a user to reduce the number of diagnoses by answering a number of queries, i.e. whether some set of formulas should be entailed by the target KB or not.

The project can be applied to debugging (diagnosis) of SAT (SAT4J), Constraints (Choco, JSolver) and OWL (any OWLAPI compatible reasoner) knowledge bases. At the moment the project team focuses on development on the OWL debugging module, therefore, CP and SAT modules might be slightly outdated.

# Protégé debugger plug-in