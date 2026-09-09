package com.palashsaathi.app.engine

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import com.palashsaathi.app.data.model.GeneratedWorksheet
import com.palashsaathi.app.data.model.LanguagePairMode
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object WorksheetPdfGenerator {

    /**
     * Generates a printable A4 bilingual FLN worksheet (Hindi/English <-> Santali) in PDF format.
     */
    fun generatePdf(
        context: Context,
        worksheet: GeneratedWorksheet,
        languageMode: LanguagePairMode = LanguagePairMode.HINDI_TO_SANTALI
    ): File {
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
        val headerTitle = if (languageMode == LanguagePairMode.ENGLISH_TO_SANTALI)
            "PalashSaathi - FLN Bilingual Worksheet (English <-> Santali)"
        else
            "PalashSaathi - FLN Bilingual Worksheet (द्विभाषी अभ्यास पत्र)"
        canvas.drawText(headerTitle, 40f, y, paintHeader)
        y += 20f
        canvas.drawText("${worksheet.getTitle(languageMode)}  |  ${worksheet.titleSantaliDevanagari} (${worksheet.titleSantaliOlChiki})", 40f, y, paintSubHeader)
        y += 15f
        canvas.drawText("Level: ${worksheet.grade.getLabel(languageMode)}  •  Category: ${worksheet.category.getLabel(languageMode)}", 40f, y, paintSubHeader)
        y += 15f

        // Date and Student info line
        val dateStr = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(worksheet.generatedTimestamp))
        canvas.drawLine(40f, y, 555f, y, paintLine)
        y += 18f
        val studentInfoText = if (languageMode == LanguagePairMode.ENGLISH_TO_SANTALI)
            "Student Name: ______________________   Date: $dateStr"
        else
            "विद्यार्थी का नाम (Name): ______________________   तारीख (Date): $dateStr"
        canvas.drawText(studentInfoText, 40f, y, paintBody)
        y += 20f
        canvas.drawLine(40f, y, 555f, y, paintLine)
        y += 30f

        // Exercises Section
        val exercisesHeaderText = if (languageMode == LanguagePairMode.ENGLISH_TO_SANTALI)
            "Exercises:"
        else
            "अभ्यास प्रश्न (Exercises):"
        canvas.drawText(exercisesHeaderText, 40f, y, paintBold)
        y += 25f

        worksheet.exercises.forEachIndexed { index, exercise ->
            val num = index + 1

            // Draw exercise box
            val boxTop = y - 14f
            val boxHeight = 55f
            canvas.drawRect(40f, boxTop, 555f, boxTop + boxHeight, paintBox)

            // Number and Question in Source Language (Hindi or English)
            canvas.drawText("$num. ${exercise.getQuestion(languageMode)}", 50f, y + 5f, paintBold)

            // Question in Santali (Devanagari + Ol Chiki representation)
            val santaliLabel = if (languageMode == LanguagePairMode.ENGLISH_TO_SANTALI) "Santali: " else "संथाली (Santali): "
            canvas.drawText("   $santaliLabel${exercise.questionSantaliDevanagari}  •  ${exercise.questionSantaliOlChiki}", 50f, y + 22f, paintBody)

            // Hint & Answer line
            val hintLabel = if (languageMode == LanguagePairMode.ENGLISH_TO_SANTALI) "Hint: " else "संकेत (Hint): "
            canvas.drawText("   $hintLabel${exercise.getHint(languageMode)} / ${exercise.hintSantali}", 50f, y + 36f, paintSubHeader)

            // Answer checkbox or write area
            canvas.drawRect(470f, y - 5f, 545f, y + 30f, paintBox)
            val ansLabel = if (languageMode == LanguagePairMode.ENGLISH_TO_SANTALI) "Ans" else "उत्तर (Ans)"
            canvas.drawText(ansLabel, 480f, y + 16f, paintSubHeader)

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
