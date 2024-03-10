package com.m3u.data.parser.internal

import android.util.Xml
import com.m3u.data.parser.EpgParser
import org.xmlpull.v1.XmlPullParser
import java.io.InputStream

data class EpgChannel(
    val id: String,
    val displayName: String
)

data class EpgProgramme(
    val channel: String,
    val start: String,
    val stop: String,
    val title: String,
    val desc: String,
    val icon: String,
    val category: String
)

class EpgParserImpl : EpgParser {
    override suspend fun execute(
        input: InputStream,
        callback: (count: Int, total: Int) -> Unit
    ): String {
        val parser = Xml.newPullParser()
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
        parser.setInput(input, null)
        return readChannel(parser)
    }

    private val ns: String? = null
    private fun readEpg(parser: XmlPullParser): List<String> = buildList {
        parser.require(XmlPullParser.START_TAG, ns, "tv")
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            when (parser.name) {
                "programme" -> add(readProgramme(parser))
                "channel" -> add(readChannel(parser))
                else -> skip(parser)
            }
        }
    }

    private fun readProgramme(parser: XmlPullParser): String {
        TODO()
    }

    private fun skip(parser: XmlPullParser) {
        TODO()
    }

    private fun readChannel(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, ns, "channel")
        parser.getAttributeValue(null, "display-name")
        parser.require(XmlPullParser.END_TAG, ns, "channel")
        TODO()
    }
}