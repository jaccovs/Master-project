package org.exquisite.protege.model.search;

import org.protege.editor.owl.model.search.SearchMetadataImporter;

import java.util.ArrayList;
import java.util.List;

class DebuggerSearchMetadataImportManager {

    List<SearchMetadataImporter> getImporters() {
        List<SearchMetadataImporter> importers = new ArrayList<>();
        importers.add(new DebuggerSearchMetadataImporter());
        return importers;
    }

}
