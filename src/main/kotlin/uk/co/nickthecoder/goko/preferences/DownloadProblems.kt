/*
GoKo a Go Client
Copyright (C) 2017 Nick Robinson

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/
package uk.co.nickthecoder.goko.preferences

import javafx.application.Platform
import javafx.scene.control.Alert
import uk.co.nickthecoder.goko.util.Unzipper
import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.ParameterException
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.BooleanParameter
import uk.co.nickthecoder.paratask.parameters.ChoiceParameter
import uk.co.nickthecoder.paratask.parameters.StringParameter
import uk.co.nickthecoder.paratask.parameters.compound.ResourceParameter
import uk.co.nickthecoder.paratask.util.child
import uk.co.nickthecoder.paratask.util.process.Exec
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.URL

class DownloadProblems(val problemsDirectory: File) : AbstractTask() {

    override val taskD = TaskDescription("downloadProblems")

    val choicesP = ChoiceParameter<DownloadInfo>("goProblems", value = DownloadInfo("", "", true))

    val nameP = StringParameter("name")

    val urlP = ResourceParameter("location")

    val isZipFileP = BooleanParameter("isZipFile")

    init {
        taskD.addParameters(choicesP, nameP, urlP, isZipFileP)

        choicesP.listen {
            nameP.value = choicesP.value?.name ?: ""
            urlP.stringValue = choicesP.value?.urlString ?: ""
            isZipFileP.value = choicesP.value?.isZip ?: true
        }

        addChoice("", "")
        addChoice("Go Game Guru", "https://gogameguru.com/i/go-problems/download/weekly-go-problems.zip")
        addChoice("Qi Jing Zhong Miao", "http://dl.u-go.net/problems/qjzm-a.zip")
        addChoice("Xuan Xuan Qi Jing", "http://dl.u-go.net/problems/xxqj.zip")
        addChoice("Guan Zi Pu - Set 1", "http://dl.u-go.net/problems/gzp1.zip")
        addChoice("Guan Zi Pu - Set 2", "http://dl.u-go.net/problems/gzp2.zip")
        addChoice("Guan Zi Pu - Set 3", "http://dl.u-go.net/problems/gzp3.zip")

        addChoice("Cho Chikun Elementary", "https://github.com/mango314/scrapeGo/raw/master/cho-1-elementary.sgf", false)
        addChoice("Cho Chikun Intermediate", "https://github.com/mango314/scrapeGo/raw/master/cho-2-intermediate.sgf", false)
        addChoice("Cho Chikun Advanced", "https://github.com/mango314/scrapeGo/raw/master/cho-3-advanced.sgf", false)
        addChoice("Gokyo Shumyo", "https://github.com/mango314/scrapeGo/raw/master/gokyoshumyo.sgf", false)
        addChoice("Hatsuyoron", "https://github.com/mango314/scrapeGo/blob/master/hatsuyoron.sgf", false)

    }

    override fun customCheck() {
        super.customCheck()
        val directory = File(problemsDirectory, nameP.value)

        if (directory.exists()) {
            throw ParameterException(nameP, "Already downloaded")
        }
    }

    private fun addChoice(name: String, urlString: String, isZip: Boolean = true) {
        choicesP.addChoice(name, DownloadInfo(name, urlString, isZip), name)
    }

    /**
     * For some reason the Go Gam Guru collection returned a 403 when I try to download it directly.
     * But wget works fine, this this tries the direct approach first, and then tries using wget.
     * wget will surely fail on Windows, so I guess people will have to download it manually.
     */
    private fun openUrl(url: URL): InputStream {
        try {
            return url.openStream()
        } catch (e: Exception) {
            val exec = Exec("wget", "-q", "-O", "-", url.toString())
            println(exec)
            exec.outSink = null
            exec.start()
            println("Returning ${exec.process!!.inputStream}")
            return exec.process!!.inputStream
        }
    }

    override fun run() {
        val directory = File(problemsDirectory, nameP.value)
        val url = urlP.value!!.url

        try {
            //val stream = url.openStream()
            val stream = openUrl(url)
            with(stream) {

                if (isZipFileP.value == true) {
                    println("Unzipping file")
                    val unzipper = Unzipper()
                    unzipper.unzip(stream, directory)
                    println("Done")

                } else {
                    val buffer = ByteArray(1024)

                    val file = problemsDirectory.child(nameP.value, nameP.value + ".sgf")
                    file.parentFile.mkdirs()

                    val fos = FileOutputStream(file)
                    with(fos) {
                        var len = stream.read(buffer)
                        while (len > 0) {
                            fos.write(buffer, 0, len)
                            len = stream.read(buffer)
                        }
                    }
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()

            Platform.runLater {
                val alert = Alert(Alert.AlertType.ERROR)
                alert.title = "Error"
                alert.headerText = null
                alert.contentText = "Download failed. The file may not exist."
                alert.showAndWait()
            }
        }
    }

    data class DownloadInfo(val name: String, val urlString: String, val isZip: Boolean = true)
}
