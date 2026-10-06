import fitz

doc = fitz.open('scratch/test_export.pdf')
print("Total pages in PDF:", len(doc))

headings = [
    ("bm_ch1", "CHAPTER 1"),
    ("bm_1_1", "1.1 BACKGROUND"),
    ("bm_1_2", "1.2 DRIVING QUESTION"),
    ("bm_1_3", "1.3 OBJECTIVES"),
    ("bm_1_4", "1.4 SCOPE AND LIMITATIONS"),
    ("bm_ch2", "CHAPTER 2"),
    ("bm_2_1", "2.1 RELATED APPROACHES"),
    ("bm_2_1_1", "2.1.1 TRADITIONAL SPREADSHEET"),
    ("bm_2_1_2", "2.1.2 ENTERPRISE ERP"),
    ("bm_2_2", "2.2 SUMMARY TABLE"),
    ("bm_2_3", "2.3 WHAT THIS TOLD US"),
    ("bm_ch3", "CHAPTER 3"),
    ("bm_3_1", "3.1 WEEKLY PBL PROGRESS LOG"),
    ("bm_3_2", "3.2 REQUIREMENTS"),
    ("bm_3_3", "3.3 FEASIBILITY"),
    ("bm_ch4", "CHAPTER 4"),
    ("bm_4_1", "4.1 SYSTEM ARCHITECTURE"),
    ("bm_4_2", "4.2 BASELINE"),
    ("bm_4_3", "4.3 PROJECT REFINEMENT"),
    ("bm_4_4", "4.4 FINAL APPROACH"),
    ("bm_4_5", "4.5 TESTING AND EXECUTION"),
    ("bm_ch5", "CHAPTER 5"),
    ("bm_5_1", "5.1 MODULE DESCRIPTION"),
    ("bm_5_2", "5.2 KEY CODE SNIPPETS"),
    ("bm_5_3", "5.3 USER INTERFACE / DEMO"),
    ("bm_ch6", "CHAPTER 6"),
    ("bm_6_1", "6.1 EVALUATION METRICS"),
    ("bm_6_2", "6.2 RESULTS ACROSS ITERATIONS"),
    ("bm_6_3", "6.3 DISCUSSION"),
    ("bm_6_4", "6.4 LIMITATIONS"),
    ("bm_ch7", "CHAPTER 7"),
    ("bm_7_1", "7.1 INDIVIDUAL REFLECTIONS"),
    ("bm_7_2", "7.2 TEAM LEARNING"),
    ("bm_7_3", "7.3 COURSE OUTCOMES"),
    ("bm_ch8", "CHAPTER 8"),
    ("bm_8_1", "8.1 CONCLUSION"),
    ("bm_8_2", "8.2 FUTURE SCOPE"),
    ("bm_refs", "REFERENCES"),
    ("bm_app", "APPENDIX"),
    ("bm_A_1", "A.1 FULL SOURCE CODE"),
    ("bm_A_2", "A.2 WEEKLY LOG"),
    ("bm_A_3", "A.3 SELF AND PEER ASSESSMENT")
]

# PDF page 15 corresponds to body page 1 (since 14 front-matter pages precede it)
for bm, h in headings:
    found_p = None
    for pno in range(14, len(doc)):
        txt = doc[pno].get_text()
        if h in txt:
            found_p = pno + 1 - 14 # body page number
            break
    print(f"{bm:10s} | {h:35s} -> Body Page {found_p}")
