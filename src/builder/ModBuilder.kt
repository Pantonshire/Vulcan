package builder

import io.Directories
import io.FileReader
import io.FileWriter
import java.io.File

object ModBuilder {

    var modID = "my_vulcan_mod"
    var modName = "My Vulcan Mod"
    var version = "no version provided"
    private val items: MutableList<Item> = mutableListOf()

    val src = "java${File.separator}com${File.separator}$modID"
    val assets = "resources${File.separator}assets${File.separator}$modID"

    fun registerItem(item: Item) {
        items += item
    }

    fun build() {
        if(modID.isEmpty()) {
            println("Build failed: invalid mod ID")
            return
        }

        projectSkeleton()
        modItemsFile()
        javaFileFromTemplate("VulcanMod")
        javaFileFromTemplate("Vulcan")
        javaFileFromTemplate("RegistryManager")
        javaFileFromTemplate("ItemManager")
        javaFileFromTemplate("VulcanItem")
        itemModelFiles()
    }

    private fun projectSkeleton() {
        //Source directories
        Directories.mkdir(*src.split(File.separator).toTypedArray())

        //Assets directories
        Directories.mkdir(*assets.split(File.separator).toTypedArray())
        Directories.mkdir(assets, "lang")
        Directories.mkdir(assets, "models", "item")
        Directories.mkdir(assets, "textures", "item")
    }

    private fun javaFileFromTemplate(name: String) {
        val templateLines = FileReader.readTextFile(Directories.getDirectory("templates", "$name.txt"))
        val javaFileLines: MutableList<String> = mutableListOf()
        templateLines.asSequence().forEach {
            javaFileLines += it.replace("~MODID~", modID).replace("~NAME~", modName).replace("~VERSION~", version)
        }
        FileWriter.writeFile(Directories.getDirectory(src, "$name.java"), *javaFileLines.toTypedArray())
    }

    private fun modItemsFile() {
        var content: String = "package com.$modID;¶»¶public final class ModItems {¶»public static void makeItems() {"
        items.asSequence().forEach {
            content += "¶" + it.toJava()
        }
        content += "¶»}¶}"
        FileWriter.writeFile(Directories.getDirectory(src, "ModItems.java"), content)
    }

    private fun itemModelFiles() {
        val jsonContent = "{¶»\"parent\": \"item/generated\",¶»\"textures\": { \"layer0\": \"$modID:item/~TEXTURE~\" }¶}"
        items.asSequence().forEach {
            FileWriter.writeFile(Directories.getDirectory(assets, "models", "item", "${it.registryName()}.json"),
                    jsonContent.replace("~TEXTURE~", it.registryName()))
        }
    }
}