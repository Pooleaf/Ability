package net.pooleaf.abilityreward.services

import javax.script.Invocable
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager

class JavaScriptService {

    val scriptEngineManager: ScriptEngineManager = ScriptEngineManager()
    var scriptEngine: ScriptEngine = clearScriptEngine()


    fun clearScriptEngine(): ScriptEngine {
        scriptEngine = scriptEngineManager.getEngineByName("JavaScript")
        return scriptEngine
    }

    fun callFunction(functionName: String, vararg args: Any) {
        (scriptEngine as Invocable).invokeFunction(functionName, *args)
    }

    fun eval(javascriptCode: String) {
        scriptEngine.eval(javascriptCode)
    }

}