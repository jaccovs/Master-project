package org.exquisite.protege.explanation;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: Matthew Horridge
 * Stanford University
 * Bio-Medical Informatics Research Group
 * Date: 20/03/2012
 *
 * @apiNote This is a <i>modified</i> copy from the explanation-workbench 5.0.0-beta-19
 * (Revision Number 3c2a4fa7f0591c18693d2b8a6bd0a9739dde2340) at https://github.com/protegeproject/explanation-workbench.git
 * <br>modifications: visibility changes by @author wolfi
 */
public class JustificationCacheManager {

    private Map<JustificationType, JustificationCache> caches = new HashMap<>();

    JustificationCacheManager() {
        for(JustificationType type : JustificationType.values()) {
            caches.put(type, new JustificationCache());
        }
    }

    JustificationCache getJustificationCache(JustificationType justificationType) {
        return caches.get(justificationType);
    }

    public void clear() {
        for(JustificationCache cache : caches.values()) {
            cache.clear();
        }
    }
}
