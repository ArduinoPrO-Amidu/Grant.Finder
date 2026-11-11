package com.example.data.demo

import com.example.data.model.Opportunity

/**
 * Curated demonstration records for initial exploration.
 * Clearly labeled as DEMO DATA per platform requirements until live crawler syncs.
 */
object DemoOpportunitiesData {
    val sampleOpportunities: List<Opportunity> = listOf(
        Opportunity(
            id = "demo_mofa_agri_2026",
            title = "[DEMO DATA] Ghana Youth AgriTech Innovation Grant",
            organization = "Ministry of Food and Agriculture (MoFA) & Mastercard Foundation",
            category = "Agriculture",
            shortDescription = "Seed capital and technical training for young Ghanaian agripreneurs scaling climate-smart farming solutions in Volta, Ashanti, and Northern regions.",
            fullDescription = "The Ghana Youth AgriTech Innovation Grant supports innovative youth-led agribusinesses. Successful applicants receive non-dilutive seed grants between GHS 50,000 and GHS 120,000, 6 months of tailored agronomy mentorship, and connections to major grain and vegetable value chains across Ghana.",
            fundingAmount = "GHS 50,000 – 120,000",
            currency = "GHS",
            applicationDeadline = "15 December 2026",
            opportunityStatus = "open",
            verificationStatus = "demo",
            eligibilityCriteria = listOf(
                "Ghanaian citizen aged between 18 and 35 years",
                "Operating an active agribusiness or AgriTech prototype in Ghana",
                "Demonstrated potential for youth job creation",
                "Willingness to complete a 4-week hybrid incubation program"
            ),
            eligibleApplicantTypes = listOf("Farmer", "Entrepreneur", "Youth", "Business owner"),
            requiredDocuments = listOf(
                "Ghana Card (National ID)",
                "Business Registration Certificate (Registrar General's Department)",
                "1-Page Project Summary & Budget Breakdown",
                "Proof of Operational Land or Facility Agreement"
            ),
            openingDate = "1 October 2026",
            applicationProcedure = "Submit digital application with pitch deck and financial projection via the official MoFA Youth Portal.",
            officialSource = "Ministry of Food and Agriculture Ghana",
            officialUrl = "https://mofa.gov.gh",
            lastVerificationDate = "10 November 2026"
        ),
        Opportunity(
            id = "demo_gnpc_stem_scholarship",
            title = "[DEMO DATA] GNPC Foundation STEM Tertiary Scholarship",
            organization = "Ghana National Petroleum Corporation (GNPC) Foundation",
            category = "Scholarships",
            shortDescription = "Full tuition and living stipend grant for undergraduate and postgraduate STEM scholars at recognized public tertiary institutions in Ghana.",
            fullDescription = "The GNPC Foundation awards educational grants to brilliant and needy Ghanaian students pursuing accredited degrees in Science, Technology, Engineering, Mathematics, and Artificial Intelligence. The grant covers 100% academic fees, campus accommodation, book allowance, and a monthly stipend.",
            fundingAmount = "Full Tuition + GHS 18,000/yr Stipend",
            currency = "GHS",
            applicationDeadline = "30 November 2026",
            opportunityStatus = "closing_soon",
            verificationStatus = "demo",
            eligibilityCriteria = listOf(
                "Ghanaian citizen with verified permanent residence in Ghana",
                "Unconditional admission offer from an accredited public Ghanaian university",
                "WASSCE aggregate score of 6 to 15 (Undergraduate) or 2nd Class Upper (Postgraduate)",
                "Financial hardship justification"
            ),
            eligibleApplicantTypes = listOf("Student", "Researcher", "Individual"),
            requiredDocuments = listOf(
                "Admission Letter and Fee Schedule",
                "WASSCE / Tertiary Transcripts",
                "Ghana Card",
                "Endorsement letter from District Assembly or Community Leader"
            ),
            openingDate = "15 September 2026",
            applicationProcedure = "Complete the online scholarship portal application and upload verified scanned credentials.",
            officialSource = "GNPC Foundation Educational Portal",
            officialUrl = "https://gnpcfoundation.org",
            lastVerificationDate = "8 November 2026"
        ),
        Opportunity(
            id = "demo_women_tech_accra",
            title = "[DEMO DATA] Women in Tech Ghana Scaleup Fund",
            organization = "MEST Africa & GIZ Ghana",
            category = "Women empowerment",
            shortDescription = "Equity-free expansion grants and executive leadership acceleration for women-founded technology ventures in Ghana.",
            fullDescription = "Designed to bridge the gender financing gap in African tech, the Women in Tech Scaleup Fund provides catalytic grants of up to GHS 150,000, access to international investor networks, and dedicated CTO advisory for software, fintech, and digital commerce startups led by women.",
            fundingAmount = "GHS 80,000 – 150,000",
            currency = "GHS",
            applicationDeadline = "10 January 2027",
            opportunityStatus = "open",
            verificationStatus = "demo",
            eligibilityCriteria = listOf(
                "Company must have at least 50% female ownership or female founder/co-founder",
                "Registered business entity operating in Ghana for at least 12 months",
                "Tech-enabled product with verified monthly recurring revenue",
                "Commitment to recruit and mentor female interns"
            ),
            eligibleApplicantTypes = listOf("Entrepreneur", "Business owner", "Individual"),
            requiredDocuments = listOf(
                "Business Incorporation Certificate and Form A",
                "Audited Accounts or 12-month Bank Statements",
                "Product Demo Video (3 minutes)",
                "Founder CVs and Ghana Cards"
            ),
            openingDate = "1 November 2026",
            applicationProcedure = "Apply through MEST Africa portal with traction metrics and 12-month milestone projections.",
            officialSource = "MEST Africa Initiatives",
            officialUrl = "https://meltwater.org",
            lastVerificationDate = "5 November 2026"
        ),
        Opportunity(
            id = "demo_gexim_sme_fund",
            title = "[DEMO DATA] GEXIM SME Value-Addition Growth Facility",
            organization = "Ghana Export-Import Bank (GEXIM)",
            category = "Business grants",
            shortDescription = "Subsidized capital grant and equipment acquisition financing for local manufacturers producing made-in-Ghana products.",
            fullDescription = "The GEXIM SME Value-Addition Facility accelerates domestic industrial transformation. Grants are provided for food processing, garment manufacturing, pharmaceutical, and cosmetic manufacturers to acquire modern packaging machinery and international certification (FDA / GSA / ISO).",
            fundingAmount = "GHS 100,000 – 350,000",
            currency = "GHS",
            applicationDeadline = "20 January 2027",
            opportunityStatus = "open",
            verificationStatus = "demo",
            eligibilityCriteria = listOf(
                "100% Ghanaian-owned SME",
                "Active production facility in any of the 16 regions of Ghana",
                "Commitment to export to AfCFTA markets or substitute imports",
                "Valid Ghana Standards Authority or FDA certification (or in process)"
            ),
            eligibleApplicantTypes = listOf("Business owner", "Entrepreneur", "Artisan"),
            requiredDocuments = listOf(
                "Registrar General Certificate and Company Regulations",
                "Tax Clearance Certificate (GRA)",
                "Equipment Pro-forma Invoices",
                "Business Plan & Production Capacity Report"
            ),
            openingDate = "20 October 2026",
            applicationProcedure = "Download application forms or apply through the GEXIM digital portal, submitting hard copies to regional offices.",
            officialSource = "Ghana Export-Import Bank",
            officialUrl = "https://eximbankghana.com",
            lastVerificationDate = "9 November 2026"
        ),
        Opportunity(
            id = "demo_cocobod_cocoa_youth",
            title = "[DEMO DATA] COCOBOD Next-Gen Cocoa Farmers Grant",
            organization = "Ghana Cocoa Board (COCOBOD)",
            category = "Youth empowerment",
            shortDescription = "Free improved hybrid seedlings, solar irrigation equipment, and living subsidies for new young cocoa farm developers.",
            fullDescription = "COCOBOD's Next-Gen initiative incentivizes youth participation in sustainable cocoa forestry. Beneficiaries receive free disease-resistant hybrid cocoa seedlings, subsidized biological fertilizers, boreholes, and cash stipends during the initial planting cycles.",
            fundingAmount = "Free Inputs + GHS 30,000 Setup Grant",
            currency = "GHS",
            applicationDeadline = "28 February 2027",
            opportunityStatus = "upcoming",
            verificationStatus = "demo",
            eligibilityCriteria = listOf(
                "Youth aged 18 to 40",
                "Access to at least 2 acres of documented arable land in cocoa growing districts",
                "Participation in Good Agricultural Practices (GAP) training"
            ),
            eligibleApplicantTypes = listOf("Farmer", "Youth", "Individual"),
            requiredDocuments = listOf(
                "Letter of Land Availability from Chief / Landowner",
                "Ghana Card",
                "District COCOBOD Extension Officer Verification"
            ),
            openingDate = "1 December 2026",
            applicationProcedure = "Register with the local District Cocoa Officer or through the COCOBOD farmer registration app.",
            officialSource = "Ghana Cocoa Board",
            officialUrl = "https://cocobod.gh",
            lastVerificationDate = "7 November 2026"
        ),
        Opportunity(
            id = "demo_gsdf_skills_dev",
            title = "[DEMO DATA] Ghana Skills Development Fund (GSDF) Cycle 4",
            organization = "Commission for TVET (CTVET) Ghana",
            category = "Skills development",
            shortDescription = "Institutional grants for master craftspersons, technical institutes, and trade associations upgrading artisanal skills.",
            fullDescription = "The GSDF provides matching grants to modernize vocational training. Grants support acquisition of modern diagnostic equipment, safety gear, solar installations, and digital trade certifications for Ghanaian artisans, mechanics, seamstresses, electricians, and carpenters.",
            fundingAmount = "GHS 40,000 – 180,000",
            currency = "GHS",
            applicationDeadline = "18 December 2026",
            opportunityStatus = "closing_soon",
            verificationStatus = "demo",
            eligibilityCriteria = listOf(
                "Registered Trade Association, Master Artisan, or Accredited TVET Centre",
                "Minimum 2 years of active training delivery in Ghana",
                "Focus on informal sector upskilling and green technologies"
            ),
            eligibleApplicantTypes = listOf("Artisan", "NGO", "Business owner", "Individual"),
            requiredDocuments = listOf(
                "Trade Association Membership / Master Craftsperson Certificate",
                "Proposed Training Curriculum & Budget",
                "List of Target Apprentices"
            ),
            openingDate = "15 October 2026",
            applicationProcedure = "Submit proposal directly through the CTVET GSDF online portal.",
            officialSource = "Commission for TVET Ghana",
            officialUrl = "https://gsdf.ctvet.gov.gh",
            lastVerificationDate = "4 November 2026"
        ),
        Opportunity(
            id = "demo_wacsi_ngo_resilience",
            title = "[DEMO DATA] Civil Society Grassroots Resiliency Grant",
            organization = "West Africa Civil Society Institute (WACSI) & STAR-Ghana",
            category = "NGO funding",
            shortDescription = "Core operational funding and community advocacy grants for registered local non-profit organizations and CBOs in Ghana.",
            fullDescription = "Empowering Ghanaian NGOs working on social justice, disability inclusion, rural education, and maternal healthcare. Provides unrestricted core grants alongside governance training, digital security infrastructure, and impact reporting toolkits.",
            fundingAmount = "GHS 60,000 – 200,000",
            currency = "GHS",
            applicationDeadline = "15 January 2027",
            opportunityStatus = "open",
            verificationStatus = "demo",
            eligibilityCriteria = listOf(
                "Registered local Non-Governmental Organization (NGO) or CBO in Ghana",
                "Valid Department of Social Welfare Certificate",
                "Transparent governance board and demonstrable community impact"
            ),
            eligibleApplicantTypes = listOf("NGO", "Community development"),
            requiredDocuments = listOf(
                "NGO Registration Certificate & Social Welfare Permit",
                "Audited Financials or 2-year Statement of Accounts",
                "Project Concept Note",
                "3 Professional References"
            ),
            openingDate = "5 October 2026",
            applicationProcedure = "Online grant submission on WACSI portal with project theory of change.",
            officialSource = "WACSI & STAR-Ghana Foundation",
            officialUrl = "https://wacsi.org",
            lastVerificationDate = "11 November 2026"
        ),
        Opportunity(
            id = "demo_tef_entrepreneurship",
            title = "[DEMO DATA] Tony Elumelu Foundation Entrepreneurship Programme (Ghana)",
            organization = "Tony Elumelu Foundation (TEF)",
            category = "International opportunities",
            shortDescription = "$5,000 non-refundable seed capital, world-class business training, and Pan-African networking for emerging innovators.",
            fullDescription = "Africa's leading philanthropic initiative committed to empowering young African entrepreneurs. Selected Ghanaian cohort members receive 12 weeks of intensive business development training, dedicated mentors, and $5,000 seed funding upon milestone graduation.",
            fundingAmount = "$5,000 (Approx. GHS 75,000)",
            currency = "USD",
            applicationDeadline = "1 March 2027",
            opportunityStatus = "upcoming",
            verificationStatus = "demo",
            eligibilityCriteria = listOf(
                "Ghanaian resident aged 18 and older",
                "Early-stage business idea or venture under 3 years old",
                "Must be scalable and commercially viable in Africa"
            ),
            eligibleApplicantTypes = listOf("Entrepreneur", "Student", "Business owner", "Individual"),
            requiredDocuments = listOf(
                "Valid National ID (Ghana Card or Passport)",
                "Detailed Business Plan & Pitch Video",
                "Financial projections"
            ),
            openingDate = "1 January 2027",
            applicationProcedure = "Apply on TEFConnect.com and complete the interactive aptitude quiz.",
            officialSource = "Tony Elumelu Foundation",
            officialUrl = "https://tonyelumelufoundation.org",
            lastVerificationDate = "6 November 2026"
        )
    )
}
