package com.palashsaathi.app.engine

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import com.palashsaathi.app.data.model.GeneratedWorksheet
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object WorksheetPdfGenerator {

    /**
     * Generates a printable A4 bilingual FLN worksheet (Hindi <-> Ho) in PDF format.
     */
    fun generatePdf(context: Context, worksheet: GeneratedWorksheet): File {
        val document = PdfDocument()

        // Standard A4 page size in points: 595 x 842
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = document.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        val paintHeader = Paint().apply {
            color = Color.BLACK
            textSize = 18f
            isFakeBoldText = true
        }

        val paintSubHeader = Paint().apply {
            color = Color.DKGRAY
            textSize = 12f
        }

        val paintBody = Paint().apply {
            color = Color.BLACK
            textSize = 11f
        }

        val paintBold = Paint().apply {
            color = Color.BLACK
            textSize = 11f
            isFakeBoldText = true
        }

        val paintLine = Paint().apply {
            color = Color.LTGRAY
            strokeWidth = 1.5f
        }

        val paintBox = Paint().apply {
            color = Color.BLACK
            style = Paint.Style.STROKE
            strokeWidth = 1f
        }

        var y = 40f

        // Title & Header
        canvas.drawText("PalashSaathi - FLN Bilingual Worksheet (द्विभाषी अभ्यास पत्र)", 40f, y, paintHeader)
        y += 20f
        canvas.drawText("${worksheet.titleHindi}  |  ${worksheet.titleSantaliDevanagari} (${worksheet.titleSantaliOlChiki})", 40f, y, paintSubHeader)
        y += 15f
        canvas.drawText("Level: ${worksheet.grade.label}  •  Category: ${worksheet.category.label}", 40f, y, paintSubHeader)
        y += 15f

        // Date and Student info line
        val dateStr = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(worksheet.generatedTimestamp))
        canvas.drawLine(40f, y, 555f, y, paintLine)
        y += 18f
        canvas.drawText("विद्यार्थी का नाम (Name): ______________________   तारीख (Date): $dateStr", 40f, y, paintBody)
        y += 20f
        canvas.drawLine(40f, y, 555f, y, paintLine)
        y += 30f

        // Exercises Section
        canvas.drawText("अभ्यास प्रश्न (Exercises):", 40f, y, paintBold)
        y += 25f

        worksheet.exercises.forEachIndexed { index, exercise ->
            val num = index + 1

            // Draw exercise box
            val boxTop = y - 14f
            val boxHeight = 55f
            canvas.drawRect(40f, boxTop, 555f, boxTop + boxHeight, paintBox)

            // Number and Question in Hindi
            canvas.drawText("$num. ${exercise.questionHindi}", 50f, y + 5f, paintBold)

            // Question in Santali (Devanagari + Ol Chiki representation)
            canvas.drawText("   संथाली (Santali): ${exercise.questionSantaliDevanagari}  •  ${exercise.questionSantaliOlChiki}", 50f, y + 22f, paintBody)

            // Hint & Answer line
            canvas.drawText("   संकेत (Hint): ${exercise.hintHindi} / ${exercise.hintSantali}", 50f, y + 36f, paintSubHeader)

            // Answer checkbox or write area
            canvas.drawRect(470f, y - 5f, 545f, y + 30f, paintBox)
            canvas.drawText("उत्तर (Ans)", 480f, y + 16f, paintSubHeader)

            y += boxHeight + 15f
        }

        // Footer Instructions
        y = 800f
        canvas.drawLine(40f, y, 555f, y, paintLine)
        y += 15f
        canvas.drawText("PalashSaathi • Mother-Tongue-Based Multilingual Education (MTB-MLE) • 100% Offline Generation", 40f, y, paintSubHeader)

        document.finishPage(page)

        // Save PDF to cache or files directory
        val outputDir = File(context.filesDir, "worksheets").apply { mkdirs() }
        val outputFile = File(outputDir, "Worksheet_${worksheet.id}_${System.currentTimeMillis()}.pdf")
        FileOutputStream(outputFile).use { fos ->
            document.writeTo(fos)
        }
        document.close()

        return outputFile
    }
}
