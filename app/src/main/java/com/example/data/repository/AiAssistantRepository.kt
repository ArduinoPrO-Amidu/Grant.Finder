package com.example.data.repository

import android.util.Log
import com.example.data.model.EligibilityResult
import com.example.data.model.Opportunity
import com.example.data.model.UserProfile
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withTimeout
import org.json.JSONObject

private const val TAG = "AiAssistantRepo"

class AiAssistantRepository {

    // Structured JSON model for Eligibility Assessment
    private val eligibilitySchema = Schema.obj(
        properties = mapOf(
            "score" to Schema.integer(description = "Match score percentage between 0 and 100"),
            "verdict" to Schema.string(description = "Short verdict label: Highly Eligible, Partially Eligible, or Likely Ineligible"),
            "requirementsMet" to Schema.array(
                items = Schema.string(),
                description = "Criteria clearly met based on user profile"
            ),
            "requirementsNotMet" to Schema.array(
                items = Schema.string(),
                description = "Criteria not met or outside scope"
            ),
            "informationNeeded" to Schema.array(
                items = Schema.string(),
                description = "Additional documents or facts required"
            ),
            "explanation" to Schema.string(description = "Honest, objective assessment explanation"),
            "recommendedNextSteps" to Schema.array(
                items = Schema.string(),
                description = "Actionable steps to take"
            )
        )
    )

    private val structuredModel by lazy {
        val config = generationConfig {
            responseMimeType = "application/json"
            responseSchema = eligibilitySchema
        }
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            generationConfig = config
        )
    }

    // General conversational model for Proposal and Application writing advice
    private val chatModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash"
        )
    }

    suspend fun checkEligibility(
        profile: UserProfile,
        opportunity: Opportunity
    ): Result<EligibilityResult> = withTimeout(60_000L) {
        try {
            val prompt = """
                You are Ghana GrantFinder's expert eligibility evaluation system.
                Evaluate the candidate's profile against the grant opportunity criteria.
                Be realistic, objective, and accurate. Do not fabricate requirements.
                Notice: AI eligibility scores are estimates and not official approval decisions.

                OPPORTUNITY:
                Title: ${opportunity.title}
                Offering Organization: ${opportunity.organization}
                Category: ${opportunity.category}
                Eligibility Criteria: ${opportunity.eligibilityCriteria.joinToString("; ")}
                Eligible Applicant Types: ${opportunity.eligibleApplicantTypes.joinToString(", ")}
                Required Documents: ${opportunity.requiredDocuments.joinToString(", ")}

                CANDIDATE PROFILE:
                Full Name: ${profile.fullName}
                Region in Ghana: ${profile.region}
                District: ${profile.district}
                Occupation: ${profile.occupation}
                User Type: ${profile.userType}
                Education Level: ${profile.educationLevel}
                Interests: ${profile.interests.joinToString(", ")}

                Assess match score (0-100), requirements met, requirements not met, missing info, and clear next steps.
            """.trimIndent()

            val response = structuredModel.generateContent(prompt)
            val jsonText = response.text ?: throw IllegalStateException("AI returned an empty response")

            val json = JSONObject(jsonText)
            val score = json.optInt("score", 50)
            val verdict = json.optString("verdict", "Under Assessment")
            val explanation = json.optString("explanation", "")

            val metArray = json.optJSONArray("requirementsMet")
            val metList = if (metArray != null) {
                (0 until metArray.length()).map { metArray.getString(it) }
            } else emptyList()

            val notMetArray = json.optJSONArray("requirementsNotMet")
            val notMetList = if (notMetArray != null) {
                (0 until notMetArray.length()).map { notMetArray.getString(it) }
            } else emptyList()

            val infoArray = json.optJSONArray("informationNeeded")
            val infoList = if (infoArray != null) {
                (0 until infoArray.length()).map { infoArray.getString(it) }
            } else emptyList()

            val stepsArray = json.optJSONArray("recommendedNextSteps")
            val stepsList = if (stepsArray != null) {
                (0 until stepsArray.length()).map { stepsArray.getString(it) }
            } else emptyList()

            Result.success(
                EligibilityResult(
                    score = score,
                    verdict = verdict,
                    requirementsMet = metList,
                    requirementsNotMet = notMetList,
                    informationNeeded = infoList,
                    explanation = explanation,
                    recommendedNextSteps = stepsList
                )
            )
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Eligibility check timed out", e)
            Result.failure(Exception("Assessment timed out. Please check your connection and try again."))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Eligibility check failed", e)
            Result.failure(e)
        }
    }

    /**
     * Streams responses for grant application assistance, proposal writing,
     * budgeting, and grant requirement advisory.
     */
    fun streamAdvice(
        userMessage: String,
        opportunityContext: Opportunity? = null
    ): Flow<String> = flow {
        val systemInstruction = """
            You are the Ghana GrantFinder AI Application Assistant.
            You provide professional, practical, and culturally tailored guidance for Ghanaians
            applying for grants, scholarships, agriculture initiatives, and business funds.
            You help with:
            - Business proposal outlines
            - Project descriptions and SMART objectives
            - Grant budgets and justification
            - Application letters and personal statements
            - Required documentation checklists (Ghana Card, Registrar General, GRA, etc.)
            Always provide high-quality, actionable advice formatted with clear headings or bullet points.
            Never claim that an application has been officially submitted or approved.
        """.trimIndent()

        val prompt = if (opportunityContext != null) {
            """
            $systemInstruction

            Current Opportunity Context:
            Title: ${opportunityContext.title}
            Organization: ${opportunityContext.organization}
            Category: ${opportunityContext.category}
            Funding: ${opportunityContext.fundingAmount}
            Deadline: ${opportunityContext.applicationDeadline}
            Criteria: ${opportunityContext.eligibilityCriteria.joinToString("; ")}
            Documents Required: ${opportunityContext.requiredDocuments.joinToString(", ")}

            User Question: $userMessage
            """.trimIndent()
        } else {
            """
            $systemInstruction

            User Question: $userMessage
            """.trimIndent()
        }

        withTimeout(60_000L) {
            chatModel.generateContentStream(prompt).collect { chunk ->
                val text = chunk.text
                if (!text.isNullOrEmpty()) {
                    emit(text)
                }
            }
        }
    }.flowOn(Dispatchers.IO)
}
