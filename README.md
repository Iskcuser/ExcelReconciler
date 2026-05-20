# Excel Checker

A lightweight, memory-efficient Java desktop application built using Swing and Apache POI to automate the comparison of numeric columns in Excel spreadsheets (.xlsx).

The application scans two user-specified columns row by row, standardizes and parses numeric text, matches values rounded to two decimal places, highlights matching data cells with a custom green fill, and saves the results into a new, unique workbook file.

---

## Technical Architecture & Core Modules

The project is structured into distinct packages following standard Java modular practices, optimizing for performance, clean architecture, and decoupled responsibilities:

1. com.excelchecker (Main)
   * Initializes the system look-and-feel (e.g., matching the host OS like Windows Explorer) and invokes the main Graphical User Interface asynchronously.
2. gui (ExcelCheckerGUI)
   * A responsive, single-frame desktop interface using a GridBagLayout.
   * Features a standard file chooser filtered exclusively to .xlsx files, configurable source/target column field mapping, a real-time tracking progress bar, dynamic text animation indicators, and error alert dialogs.
   * Offloads CPU-intensive worksheet parsing tasks to a background thread using SwingWorker to ensure the interface stays fluid and uninterrupted during big file streams.
3. reader (ExcelReader)
   * Safely reads raw data using WorkbookFactory.create(new File(filePath)). Instantiating workbook instances via a File object rather than an InputStream is a crucial choice that minimizes memory footprints for large spreadsheet operations.
   * Implements Row.MissingCellPolicy.RETURN_BLANK_AS_NULL to prevent building bloated, empty memory reference objects during sheet iterations.
4. cellValueExtractor (CellValueExtractor)
   * Handles typed extractions using Java modern expression switches. It standardizes dynamic formatting configurations like STRING, NUMERIC, BOOLEAN, dates, and embeds an active FormulaEvaluator to resolve programmatic runtime cell functions natively.
5. comparator (ExcelComparator)
   * Sanitizes raw string inputs by pruning non-breaking spaces (\u00A0), normalizes regional locale decimals (swapping commas , to dots .), rounds double precision values up to exactly two decimal places, and executes precise bitwise numeric comparisons.
6. painter (ExcelPainter)
   * Handles data rendering by instantiating a custom reusable XSSFCellStyle. Matches corresponding values with a pleasant, solid foreground fill color (Hex #92D050 Soft Green).
7. model (CompareResult)
   * Leverages standard immutable Java record declarations containing nested internal state tokens (MATCH, SKIP).
8. util / exception (FileSaver & ExcelException)
   * Manages decoupled FileOutputStream resource sequences and provides runtime exceptions for failure scenarios.

---

## Features & Visual Highlights

* Asynchronous Processing: Utilizes SwingWorker thread pooling so processing massive spreadsheets won't lock up or freeze the desktop view.
* Dynamic UX Adjustments: Includes an independent animated javax.swing.Timer that updates textual feedback strings, along with self-throttling micro-pauses (Thread.sleep) which automatically adapt processing rates based on file depth.
* Non-Destructive Workflows: Generates unique matching filenames (*result.xlsx, *result(1).xlsx, etc.) to prevent accidentally overwriting critical historical document source logs.
* Robust Pre-Processing & Header Skipping: Strips trailing characters or non-breaking whitespaces while identifying string headers automatically via explicit parsing catch triggers.

---

## Prerequisites

* Java Development Kit (JDK): Version 17 or higher (Leverages modern switch expressions and records).
* Build Tool / Dependencies: Standard Java build tools configured with the following dependencies:
  * Apache POI (Core & OOXML schemas for processing modern .xlsx workbooks).

---

## Project Structure
```text
src/
├── cellValueExtractor/
│   └── CellValueExtractor.java
├── com/
│   └── excelchecker/
│       └── Main.java
├── comparator/
│   └── ExcelComparator.java
├── exception/
│   └── ExcelException.java
├── gui/
│   └── ExcelCheckerGUI.java
├── model/
│   └── CompareResult.java
├── painter/
│   └── ExcelPainter.java
├── reader/
│   └── ExcelReader.java
└── util/
    └── FileSaver.java
```
---

## Execution & Usage

### 1. Launching the App
Run the compiled Main class. The GUI panel will open at the center of your screen:

java com.excelchecker.Main

### 2. Operating the Application
1. Choose Spreadsheet: Click the "Выбрать(Choose)..." button to open up the native file chooser and select your target .xlsx file.
2. Map Columns: Input your alphabetical target columns inside "Колонна 1(Column 1)" and "Колонна 2(Column 2)" input parameters (e.g., column D and H).
3. Run Comparison: Click "► Запустить(Launch)". The progress bar will reflect real-time progress.
4. Review Output: Once processing is complete, a success dialog box will pop up summarizing the total count of processed rows and confirmed value matches. The newly painted spreadsheet will be saved next to your source file.


---
---

## Final Note

* **GUI Language:** Russian (All buttons, text fields, and pop-up messages inside the desktop application).
* **Codebase Language:** English.
---
