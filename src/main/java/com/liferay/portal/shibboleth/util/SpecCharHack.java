/*
 Created by Mihály Héder
 */

package com.liferay.portal.shibboleth.util;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author merlin
 */
public class SpecCharHack {
    
    public Map<String,String> m = new HashMap<String, String>();    
    
    public SpecCharHack() {
        m.put("Ã", "Á");
        m.put("Ã", "Í");
        m.put("Å°","Ű");
        m.put("Å","Ő");
        m.put("Ã","Ü");
        m.put("Ã","Ö");
        m.put("Ã","Ú");
        m.put("Ã","Ó");
        m.put("Ã","É");
        m.put("Ã¡","á");
        m.put("Ã­","í");
        m.put("Å±","ű");
        m.put("Å","ő");
        m.put("Ã¼","ü");
        m.put("Ã¶","ö");
        m.put("Ãº","ú");
        m.put("Ã³","ó");
        m.put("Ã©","é");
    }
    
    public String fixChars(String in) {
        for (String key:m.keySet()) {
            in = in.replaceAll(key, m.get(key));
        }
        return in;
    }
}
