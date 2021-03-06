package language.objects

import language.DataType

class Player(name: String, java: String = name, cas: String? = null, mutable: Boolean = false): VulcanObject(DataType.PLAYER, name, java, cas, mutable) {

    override val actions: Map<String, Int> = mapOf(
            Pair("jump", 0),        //Make player jump
            Pair("take", 2),        //Damage player
            Pair("burn", 3),        //Set player on fire
            Pair("teleport", 2),    //Set player's position
            Pair("breathe", 0),     //Restore player's air
            Pair("swing", 2),       //Swing arm
            Pair("heal", 2),        //Recover health
            Pair("die", 0),         //Take unblockable fatal damage
            Pair("ride", 1),        //Ride an entity
            Pair("explode", 3),     //Explode the player
            Pair("launch", 3)       //Launch the player in the direction they're looking
    )

    override fun convertMessage(message: String, parameters: Array<String>, variables: Map<String, VulcanObject>): String {
        when(message) {
            //Jump
            "jump" -> return "if($java.onGround){ $java.jump(); }"

            //Damage
            "take" -> {
                if(parameters[1] == "damage") {
                    val damage = DataType.INTEGER.toJava(parameters[0], variables)
                    return "MessageUtils.attack($java, $damage);"
                } else {
                    throw IllegalArgumentException("invalid syntax")
                }
            }

            //Set fire
            "burn" -> {
                if(parameters[0] == "for" && (parameters[2] == "seconds" || parameters[2] == "second")) {
                    val time = DataType.INTEGER.toJava(parameters[1], variables)
                    return "$java.setFire($time);"
                } else {
                    throw IllegalArgumentException("invalid syntax")
                }
            }

            //Set the player's position
            "teleport" -> {
                if(parameters[0] == "to") {
                    val position = DataType.VECTOR3.toJava(parameters[1], variables)
                    return "$java.setPosition($position.getX(), $position.getY(), $position.getZ())"
                } else {
                    throw IllegalArgumentException("invalid syntax")
                }
            }

            //Restore air
            "breathe" -> return "$java.setAir(300);"

            //TODO: Hand data type
            //Swing arm
            "swing" -> {
                if((parameters[0] == "left" || parameters[0] == "right") && parameters[1] == "arm") {
                    val mainHand = parameters[0] == "right"
                    return "MessageUtils.swingArm($java, $mainHand);"
                } else {
                    throw IllegalArgumentException("invalid syntax")
                }
            }

            //Restore health
            "heal" -> {
                if(parameters[1] == "health") {
                    val amount = DataType.INTEGER.toJava(parameters[0], variables)
                    return "$java.heal($amount);"
                } else {
                    throw IllegalArgumentException("invalid syntax")
                }
            }

            //Instant kill
            "die" -> return "MessageUtils.kill($java);"

            //Set riding entity
            "ride" -> {
                val target = DataType.ENTITY.toJava(parameters[0], variables)
                return "$java.startRiding($target, true);"
            }

            //Explode the player (oh no!)
            "explode" -> {
                if(parameters[0] == "with" && parameters[1] == "strength") {
                    val strength = DataType.FLOAT.toJava(parameters[2], variables)
                    return "MessageUtils.explode($java, $strength);"
                }

                throw IllegalArgumentException("invalid syntax")
            }
            
            //Launch the player
            "launch" -> {
                if(parameters[0] == "with" && parameters[1] == "speed") {
                    val speed = DataType.FLOAT.toJava(parameters[2], variables)
                    return "MessageUtils.launch($java, $speed);"
                }

                throw IllegalArgumentException("invalid syntax")
            }
        }

        //Should only be called if the message is registered as valid, but has no case in the when statement
        throw IllegalArgumentException("unsupported message $message")
    }
}
