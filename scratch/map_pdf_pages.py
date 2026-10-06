import pypdf
import sys

sys.stdout.reconfigure(encoding='utf-8')

reader = pypdf.PdfReader('scratch/test_build_v2.pdf')
headings = [
    'CHAPTER 1: INTRODUCTION', '1.1 Background', '1.2 Driving Question', '1.3 Objectives', '1.4 Scope and Limitations', '1.5 Project Overview', '1.6 Organisation of the Report',
    'CHAPTER 2: CONCEPT EXPLORATION', '2.1 Related Approaches', '2.2 Summary Table', '2.3 What This Told Us',
    'CHAPTER 3: PROJECT PLANNING AND TEAM ORGANISATION', '3.1 Weekly PBL Progress Log', '3.2 Requirements', '3.3 Feasibility',
    'CHAPTER 4: SYSTEM DESIGN AND DEVELOPMENT', '4.1 System Architecture', '4.2 Component Descriptions', '4.3 Modules and Responsibilities', '4.4 Class Design', '4.5 Calculation Engine', '4.6 Data Flow', '4.7 Security Design',
    'CHAPTER 5: IMPLEMENTATION', '5.1 Core Java Classes', '5.2 Object-Oriented', '5.3 Key Code Snippets', '5.4 User Interface',
    'CHAPTER 6: RESULTS AND DISCUSSION', '6.1 Evaluation Metrics', '6.2 Results Across Iterations', '6.3 Discussion', '6.4 Limitations', '6.5 Note on Machine',
    'CHAPTER 7: TEAM REFLECTION AND LEARNING OUTCOMES', '7.1 Individual Reflections', '7.2 Team Learning', '7.3 Course Outcomes',
    'CHAPTER 8: CONCLUSION AND FUTURE SCOPE', '8.1 Conclusion', '8.2 Future Scope',
    'REFERENCES', 'APPENDIX', 'A.1 Full source code', 'A.2 Complete Weekly', 'A.3 Self and Peer'
]

# We search body pages (page index 13 onwards) so we don't match the TOC entries
found = {}
for p_idx in range(13, len(reader.pages)):
    page = reader.pages[p_idx]
    txt = page.extract_text()
    for h in headings:
        if h not in found and h in txt:
            found[h] = p_idx + 1

print('=== ACTUAL PAGE NUMBERS IN BODY OF PDF ===')
for h in headings:
    print(f"{h:50s} -> Page {found.get(h, 'NOT FOUND')}")
