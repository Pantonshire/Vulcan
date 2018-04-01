package builder

import language.*
import language.objects.VulcanObject

abstract class Builder(val fileName: String, type: String, val lines: Array<Line>, vararg defaultGlobalVariables: VulcanObject) {

    val attributes: HashMap<String, Attribute<Any>> = hashMapOf()

    /** All of the Vulcan Objects that can be referenced from anywhere. May include "self". */
    private val globalVariables: Map<String, VulcanObject>

    /** All of the local variables that can currently be referenced. */
    private val localVariables: Map<String, VulcanObject> = mapOf()

    private val validBehaviours: Map<String, Behaviour>
    private var context = "default"
    val behaviourContent: HashMap<String, MutableList<String>> = hashMapOf()

    init {
        //Make map of valid contexts with their name as a key
        val map = hashMapOf<String, Behaviour>()
        Behaviours.getValidBehaviours(type).asSequence().forEach { map[it.name] = it }
        validBehaviours = map

        //Initialise behaviour content map
        validBehaviours.keys.asSequence().forEach {
            behaviourContent[it] = mutableListOf()
        }

        //Make global variables map
        val mutableMap: HashMap<String, VulcanObject> = hashMapOf()
        defaultGlobalVariables.asSequence().forEach { mutableMap[it.name] = it }
        globalVariables = mutableMap.toMap()
    }

    fun build() {
        for(line in lines) {
            checkForErrors(line)
            updateContext(line)
            processLine(line)
        }

        passToNext()
    }

    abstract fun passToNext()

    private fun processLine(line: Line) {
        if(context == "constructor" && line is SetLine) {
            if (line.field in attributes) {
                try {
                    attributes[line.field]!!.set(line.value)
                } catch (exception: IllegalArgumentException) {
                    line.throwError(fileName, exception.message ?: "no error message provided")
                }
            }
        }

        else if(context in validBehaviours) {
            val behaviour = validBehaviours[context]
            if(behaviour != null) {
                val visibleVariables: Map<String, VulcanObject> = getVisibleVariables(behaviour)

                //Method calls
                if(line is ActionLine) {
                    if(line.target in visibleVariables) {
                        val target = visibleVariables[line.target]!!
                        if(target.isValidMessage(line.method)) {
                            var javaFunctionCall = ""
                            try {
                                javaFunctionCall = target.messageToJava(line.method, line.arguments, behaviour.parameters)
                            } catch(exception: IllegalArgumentException) {
                                line.throwError(fileName,exception.message ?: "no error message was provided")
                            }

                            if(javaFunctionCall.isNotEmpty()) {
                                behaviourContent[context]?.add(javaFunctionCall)
                            }
                        } else {
                            line.throwError(fileName,"invalid message \"${line.method}\"")
                        }
                    } else {
                        line.throwError(fileName,"invalid target for message \"${line.target}\"")
                    }
                }

                //Assignment
                else if(line is AssignmentLine) {
                    if(line.variable in visibleVariables) {
                        val variable = visibleVariables[line.variable]!!
                        if(variable.mutable) {
                            try {
                                val value = variable.type.toJava(line.value, visibleVariables)
                                behaviourContent[context]?.add("${variable.name} = $value;")
                            } catch (exception: IllegalArgumentException) {
                                line.throwError(fileName, exception.message ?: "no error message provided")
                            }
                        } else {
                            line.throwError(fileName,"\"${line.variable}\" is read-only; it cannot be reassigned")
                        }
                    } else {
                        line.throwError(fileName,"the variable \"${line.variable}\" does not exist")
                    }
                }
            }
        }
    }

    private fun checkForErrors(line: Line) {
        if(line is BlankLine) {
            line.throwError(fileName,"internal error (this is bad!)")
        }

        else when(context) {
            "default" -> {
                if(!(line is ConstructorLine || line is BehaviourLine)) {
                    line.throwError(fileName,"no behaviour defined")
                }
            }

            "constructor" -> {
                if(line is ActionLine) {
                    line.throwError(fileName,"cannot send messages in the current behaviour")
                }
            }

            in validBehaviours -> {
                if(line is SetLine) {
                    line.throwError(fileName,"cannot set attributes in the current behaviour")
                }
            }
        }
    }

    private fun updateContext(line: Line) {
        if(line is ConstructorLine) {
            context = "constructor"
        } else if(line is BehaviourLine) {
            val behaviour = line.behaviour.name
            if(behaviour in validBehaviours) {
                context = behaviour
            } else {
                line.throwError(fileName,"unrecognised behaviour \"$behaviour\"")
            }
        }
    }

    //* Returns all of the Vulcan Objects that can be referenced in the current behaviour. */
    protected fun getVisibleVariables(currentBehaviour: Behaviour): Map<String, VulcanObject> = globalVariables + localVariables + currentBehaviour.parameters
}