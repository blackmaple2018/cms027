/*
 This file is part of the OdinMS Maple Story Server
 Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc> 
 Matthias Butz <matze@odinms.de>
 Jan Christian Meyer <vimes@odinms.de>

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License version 3
 as published by the Free Software Foundation. You may not use, modify
 or distribute this program under any other version of the
 GNU Affero General Public License.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scripting;

import java.io.File;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import client.Client;
import constants.ServerProperties;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.script.ScriptException;
import tools.FileLogger;

/**
 *
 * @author Matze
 */
public abstract class AbstractScriptManager {

    protected ScriptEngine engine;
    private ScriptEngineManager sem;

    protected AbstractScriptManager() {
        sem = new ScriptEngineManager();
    }

    /**
     * <事件加载>
     */
    protected Invocable getInvocable(String path, Client c) {
        path = System.getProperty("user.dir") + "/Script/" + path;
        //System.out.println("path：" + path);
        engine = null;
        if (c != null) {
            engine = c.getScriptEngine(path);
        }
        if (engine == null) {
            File scriptFile = new File(path);
            if (!scriptFile.exists()) {
                return null;
            }
            engine = sem.getEngineByName("javascript");
            if (c != null) {
                c.setScriptEngine(path, engine);
            }
            try (InputStream fr = new FileInputStream(scriptFile)) {
                BufferedReader bf = new BufferedReader(new InputStreamReader(fr, "UTF-8"));
                if (ServerProperties.Misc.USE_JAVA8) {
                    engine.eval("load('nashorn:mozilla_compat.js');");
                }
                engine.eval(bf);
            } catch (final ScriptException | IOException t) {
                FileLogger.printError(FileLogger.INVOCABLE + path.substring(12, path.length()), t, path);
                return null;
            }
        }
        return (Invocable) engine;
    }

    protected void resetContext(String path, Client c) {
        c.removeScriptEngine(System.getProperty("user.dir") + "/Script/" + path);
    }
}
