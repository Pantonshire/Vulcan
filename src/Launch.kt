import application.VulcanBuild
import parser.VulcanParserV2
import parser.VulcanParserV3

const val VERSION = "alpha 0.2.0"

fun main(args: Array<String>) {
//    if(args.isNotEmpty()) {
//        if(args[0] == "--version") {
//            System.out.println(VERSION)
//        }
//
//        else if(args[0] == "compile") {
//            //TODO: Get input and output directories
//            val inputDir = ""
//            val outputDir = ""
//            VulcanBuild.build(inputDir)
//        }
//    }

//    val inp = "not x"
//    val split = inp.replace(Regex("(\\s+|^)(not)(\\s+)"), "!!").split("!!")
//    println(split.size)

//    val inp = "if then == true then"
//    val split = VulcanParserV2.splitLine(inp)
//    split.asSequence().forEach {
//        println(it)
//    }

//    val inp = "tell \tself's player\t\tto    jump up in the air like they just don\'t care"
//    VulcanParserV3.splitLine(inp).asSequence().forEach {
//        println(it)
//    }

//    val inp = "repeat a million times using a counter variable called bob using"
    val inp = "while condition == true do"
    VulcanParserV3.splitLine(inp).asSequence().forEach {
        println(it)
    }
}
