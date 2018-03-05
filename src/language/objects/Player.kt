package language.objects

class Player(name: String): VulcanObject(name) {

    override val validMessages: Map<String, Int> = mapOf(
            Pair("jump", 0), //Make player jump
            Pair("take", 2), //Damage player
            Pair("burn", 3), //Set player on fire
            Pair("teleport", 2), //Set player's position
            Pair("breathe", 0), //Restore player's air
            Pair("swing", 2), //Swing arm
            Pair("heal", 2), //Recover health
            Pair("die", 0), //Take unblockable fatal damage
            Pair("mount", 1) //Ride an entity
    )

    override fun convertMessage(message: String, parameters: Array<String>, others: Array<VulcanObject>): String {
        when(message) {
            //Jump
            "jump" -> return "if(${name}.onGround){ ${name}.jump(); }"

            //Damage
            "take" -> {
                if(parameters[1] == "damage") {
                    try {
                        val damage = parameters[0].toInt()
                        return "MessageUtils.attack($name, $damage);"
                    } catch(exception: NumberFormatException) {
                        throw IllegalArgumentException("${parameters[0]} is not a valid number")
                    }
                } else {
                    throw IllegalArgumentException("invalid syntax")
                }
            }

            //Set fire
            "burn" -> {
                if(parameters[0] == "for" && (parameters[2] == "seconds" || parameters[2] == "second")) {
                    try {
                        val time = parameters[1].toInt()
                        return "${name}.setFire($time);"
                    } catch(exception: NumberFormatException) {
                        throw IllegalArgumentException("${parameters[1]} is not a valid number")
                    }
                } else {
                    throw IllegalArgumentException("invalid syntax")
                }
            }

            //Restore air
            "breathe" -> return "${name}.setAir(300);"

            //Swing arm
            "swing" -> {
                if((parameters[0] == "left" || parameters[0] == "right") && parameters[1] == "arm") {
                    val mainHand = parameters[0] == "right"
                    return "MessageUtils.swingArm($name, $mainHand);"
                } else {
                    throw IllegalArgumentException("invalid syntax")
                }
            }

            //Restore health
            "heal" -> {
                if(parameters[1] == "health") {
                    try {
                        val amount = parameters[0].toInt()
                        return "${name}.heal($amount);"
                    } catch(exception: NumberFormatException) {
                        throw IllegalArgumentException("${parameters[0]} is not a valid number")
                    }
                } else {
                    throw IllegalArgumentException("invalid syntax")
                }
            }

            //Instant kill
            "die" -> return "MessageUtils.kill($name);"

            //Set riding entity
            "mount" -> {
                others.asSequence().forEach {
                    if(it.name == parameters[0]) {
                        if(it is LivingEntity || it is Player) {
                            return "${name}.startRiding(${it.name}, true);"
                        } else {
                            throw IllegalArgumentException("${it.name} is not rideable")
                        }
                    }
                }

                throw IllegalArgumentException("\"${parameters[0]}\" is not a valid target")
            }
        }

        throw IllegalArgumentException("unsupported message $message")
    }
}