package com.b47tech.cricketscore.core.export

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.b47tech.cricketscore.core.engine.InningsScorecard
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfScorecardExporter {

    private const val PAGE_WIDTH = 595 // A4 standard width (pt)
    private const val PAGE_HEIGHT = 842 // A4 standard height (pt)

    fun exportAndShareScorecard(
        context: Context,
        matchTitle: String,
        resultSummary: String?,
        innings1: InningsScorecard?,
        innings2: InningsScorecard?,
        totalOvers: Int = 20,
        ballType: String = "Leather"
    ): File? {
        val document = PdfDocument()

        val titlePaint = Paint().apply {
            color = Color.rgb(24, 76, 120) // Deep Cricket Blue
            textSize = 20f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val subTitlePaint = Paint().apply {
            color = Color.rgb(80, 80, 80)
            textSize = 10f
            typeface = Typeface.DEFAULT
            isAntiAlias = true
        }

        val sectionHeaderPaint = Paint().apply {
            color = Color.rgb(30, 30, 30)
            textSize = 13f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val tableHeaderPaint = Paint().apply {
            color = Color.WHITE
            textSize = 9.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val bodyPaint = Paint().apply {
            color = Color.rgb(35, 35, 35)
            textSize = 9f
            typeface = Typeface.DEFAULT
            isAntiAlias = true
        }

        val boldBodyPaint = Paint().apply {
            color = Color.rgb(20, 20, 20)
            textSize = 9f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val tableHeaderBgPaint = Paint().apply {
            color = Color.rgb(44, 62, 80) // Dark Slate Blue
            style = Paint.Style.FILL
        }

        val altRowBgPaint = Paint().apply {
            color = Color.rgb(245, 247, 250) // Soft gray/blue
            style = Paint.Style.FILL
        }

        val resultBoxBgPaint = Paint().apply {
            color = Color.rgb(235, 247, 238) // Light emerald green
            style = Paint.Style.FILL
        }

        val resultBoxBorderPaint = Paint().apply {
            color = Color.rgb(46, 125, 50) // Green border
            style = Paint.Style.STROKE
            strokeWidth = 1.5f
        }

        val resultTextPaint = Paint().apply {
            color = Color.rgb(27, 94, 32)
            textSize = 11f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val dividerPaint = Paint().apply {
            color = Color.rgb(220, 224, 230)
            strokeWidth = 1f
        }

        val footerPaint = Paint().apply {
            color = Color.rgb(130, 130, 130)
            textSize = 8f
            typeface = Typeface.DEFAULT
            isAntiAlias = true
        }

        // Draw Innings Function
        fun drawInnings(canvas: Canvas, innings: InningsScorecard, startY: Float): Float {
            var y = startY

            // Innings Header
            canvas.drawText("${innings.battingTeamName} Innings", 40f, y, sectionHeaderPaint)
            val scoreText = "${innings.totalRuns}/${innings.totalWickets} (${innings.oversString} Ov, RR: ${String.format(Locale.US, "%.2f", innings.runRate)})"
            val scoreWidth = boldBodyPaint.measureText(scoreText)
            canvas.drawText(scoreText, PAGE_WIDTH - 40f - scoreWidth, y, boldBodyPaint)
            y += 8f
            canvas.drawLine(40f, y, PAGE_WIDTH - 40f, y, dividerPaint)
            y += 14f

            // Batting Table Header
            canvas.drawRect(40f, y - 11f, PAGE_WIDTH - 40f, y + 5f, tableHeaderBgPaint)
            canvas.drawText("BATTER", 45f, y, tableHeaderPaint)
            canvas.drawText("DISMISSAL", 175f, y, tableHeaderPaint)
            canvas.drawText("R", 370f, y, tableHeaderPaint)
            canvas.drawText("B", 405f, y, tableHeaderPaint)
            canvas.drawText("4s", 435f, y, tableHeaderPaint)
            canvas.drawText("6s", 465f, y, tableHeaderPaint)
            canvas.drawText("SR", 500f, y, tableHeaderPaint)
            y += 15f

            // Batting Rows
            innings.batters.forEachIndexed { index, b ->
                if (index % 2 == 1) {
                    canvas.drawRect(40f, y - 10f, PAGE_WIDTH - 40f, y + 4f, altRowBgPaint)
                }
                val batterName = if (b.playerName.length > 20) b.playerName.take(18) + ".." else b.playerName
                val dismissal = if (b.dismissalText.length > 28) b.dismissalText.take(26) + ".." else b.dismissalText

                canvas.drawText(batterName, 45f, y, if (!b.isOut && b.ballsFaced > 0) boldBodyPaint else bodyPaint)
                canvas.drawText(dismissal, 175f, y, bodyPaint)
                canvas.drawText(b.runs.toString(), 370f, y, boldBodyPaint)
                canvas.drawText(b.ballsFaced.toString(), 405f, y, bodyPaint)
                canvas.drawText(b.fours.toString(), 435f, y, bodyPaint)
                canvas.drawText(b.sixes.toString(), 465f, y, bodyPaint)
                canvas.drawText(String.format(Locale.US, "%.1f", b.strikeRate), 500f, y, bodyPaint)
                y += 14f
            }

            // Extras & Total
            y += 2f
            val extrasText = "Extras: ${innings.extras.total} (w ${innings.extras.wides}, nb ${innings.extras.noBalls}, b ${innings.extras.byes}, lb ${innings.extras.legByes})"
            canvas.drawText(extrasText, 45f, y, bodyPaint)
            val totalSummary = "TOTAL: ${innings.totalRuns}/${innings.totalWickets} (${innings.oversString} Ov)"
            val totalWidth = boldBodyPaint.measureText(totalSummary)
            canvas.drawText(totalSummary, PAGE_WIDTH - 40f - totalWidth, y, boldBodyPaint)
            y += 16f

            // Bowling Table Header
            canvas.drawRect(40f, y - 11f, PAGE_WIDTH - 40f, y + 5f, tableHeaderBgPaint)
            canvas.drawText("BOWLER", 45f, y, tableHeaderPaint)
            canvas.drawText("O", 310f, y, tableHeaderPaint)
            canvas.drawText("M", 350f, y, tableHeaderPaint)
            canvas.drawText("R", 390f, y, tableHeaderPaint)
            canvas.drawText("W", 430f, y, tableHeaderPaint)
            canvas.drawText("ECON", 480f, y, tableHeaderPaint)
            canvas.drawText("DOTS", 525f, y, tableHeaderPaint)
            y += 15f

            // Bowling Rows
            innings.bowlers.forEachIndexed { index, bw ->
                if (index % 2 == 1) {
                    canvas.drawRect(40f, y - 10f, PAGE_WIDTH - 40f, y + 4f, altRowBgPaint)
                }
                val bowlerName = if (bw.playerName.length > 26) bw.playerName.take(24) + ".." else bw.playerName
                canvas.drawText(bowlerName, 45f, y, bodyPaint)
                canvas.drawText(bw.oversString, 310f, y, bodyPaint)
                canvas.drawText(bw.maidens.toString(), 350f, y, bodyPaint)
                canvas.drawText(bw.runsConceded.toString(), 390f, y, bodyPaint)
                canvas.drawText(bw.wickets.toString(), 430f, y, boldBodyPaint)
                canvas.drawText(String.format(Locale.US, "%.2f", bw.economyRate), 480f, y, bodyPaint)
                canvas.drawText(bw.dotBalls.toString(), 525f, y, bodyPaint)
                y += 14f
            }

            // Fall of Wickets
            if (innings.fallOfWickets.isNotEmpty()) {
                y += 4f
                val fowTitle = "Fall of Wickets: "
                val fowDetails = innings.fallOfWickets.joinToString(", ") {
                    "${it.runs}/${it.wicketNumber} (${it.dismissedPlayerName}, ${it.overString} ov)"
                }
                val fullFow = fowTitle + fowDetails
                val safeFow = if (fullFow.length > 105) fullFow.take(102) + "..." else fullFow
                canvas.drawText(safeFow, 45f, y, bodyPaint)
                y += 14f
            }

            y += 10f
            return y
        }

        try {
            // PAGE 1: Header + 1st Innings
            val pageInfo1 = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create()
            val page1 = document.startPage(pageInfo1)
            val canvas1 = page1.canvas

            var y = 45f

            // Header Banner
            canvas1.drawText("B47 CRICKET SCORECARD", 40f, y, titlePaint)
            val dateStr = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date())
            val dateWidth = subTitlePaint.measureText(dateStr)
            canvas1.drawText(dateStr, PAGE_WIDTH - 40f - dateWidth, y - 2f, subTitlePaint)
            y += 16f

            // Match Title & Info
            canvas1.drawText("$matchTitle  •  $totalOvers Overs Match ($ballType ball)", 40f, y, subTitlePaint)
            y += 12f
            canvas1.drawLine(40f, y, PAGE_WIDTH - 40f, y, dividerPaint)
            y += 16f

            // Result Banner (if available)
            if (!resultSummary.isNullOrBlank()) {
                canvas1.drawRoundRect(40f, y - 10f, PAGE_WIDTH - 40f, y + 16f, 6f, 6f, resultBoxBgPaint)
                canvas1.drawRoundRect(40f, y - 10f, PAGE_WIDTH - 40f, y + 16f, 6f, 6f, resultBoxBorderPaint)
                canvas1.drawText("🏆 RESULT: $resultSummary", 52f, y + 5f, resultTextPaint)
                y += 36f
            }

            // Draw 1st Innings
            if (innings1 != null) {
                y = drawInnings(canvas1, innings1, y)
            }

            // Draw 2nd Innings on Page 1 if space allows, otherwise use Page 2
            var secondInningsOnPage1 = false
            if (innings2 != null && y < 450f) {
                secondInningsOnPage1 = true
                y = drawInnings(canvas1, innings2, y)
            }

            // Footer for Page 1
            canvas1.drawLine(40f, PAGE_HEIGHT - 35f, PAGE_WIDTH - 40f, PAGE_HEIGHT - 35f, dividerPaint)
            canvas1.drawText("Generated by B47 Cricket Score  •  Developed by B47 Tech", 40f, PAGE_HEIGHT - 22f, footerPaint)
            val pageNumText = if (innings2 != null && !secondInningsOnPage1) "Page 1 of 2" else "Page 1 of 1"
            val pageNumWidth = footerPaint.measureText(pageNumText)
            canvas1.drawText(pageNumText, PAGE_WIDTH - 40f - pageNumWidth, PAGE_HEIGHT - 22f, footerPaint)

            document.finishPage(page1)

            // PAGE 2 (If 2nd Innings was not drawn on Page 1)
            if (innings2 != null && !secondInningsOnPage1) {
                val pageInfo2 = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 2).create()
                val page2 = document.startPage(pageInfo2)
                val canvas2 = page2.canvas

                var y2 = 45f
                canvas2.drawText("B47 CRICKET SCORECARD - 2ND INNINGS", 40f, y2, titlePaint)
                y2 += 16f
                canvas2.drawText(matchTitle, 40f, y2, subTitlePaint)
                y2 += 12f
                canvas2.drawLine(40f, y2, PAGE_WIDTH - 40f, y2, dividerPaint)
                y2 += 18f

                drawInnings(canvas2, innings2, y2)

                // Footer for Page 2
                canvas2.drawLine(40f, PAGE_HEIGHT - 35f, PAGE_WIDTH - 40f, PAGE_HEIGHT - 35f, dividerPaint)
                canvas2.drawText("Generated by B47 Cricket Score  •  Developed by B47 Tech", 40f, PAGE_HEIGHT - 22f, footerPaint)
                val p2NumText = "Page 2 of 2"
                val p2NumWidth = footerPaint.measureText(p2NumText)
                canvas2.drawText(p2NumText, PAGE_WIDTH - 40f - p2NumWidth, PAGE_HEIGHT - 22f, footerPaint)

                document.finishPage(page2)
            }

            // Save PDF to cache directory
            val exportDir = File(context.cacheDir, "exports").apply { mkdirs() }
            val sanitizedTitle = matchTitle.replace(Regex("[^a-zA-Z0-9_-]"), "_")
            val pdfFile = File(exportDir, "Scorecard_${sanitizedTitle}_${System.currentTimeMillis()}.pdf")

            FileOutputStream(pdfFile).use { out ->
                document.writeTo(out)
            }
            document.close()

            return pdfFile
        } catch (e: Exception) {
            e.printStackTrace()
            document.close()
            return null
        }
    }

    fun sharePdfFile(context: Context, pdfFile: File, matchTitle: String) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            pdfFile
        )

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "$matchTitle - Cricket Scorecard")
            putExtra(Intent.EXTRA_TEXT, "Detailed cricket match scorecard for $matchTitle generated by B47 Cricket Score.")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val chooser = Intent.createChooser(shareIntent, "Share Scorecard PDF")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }
}
