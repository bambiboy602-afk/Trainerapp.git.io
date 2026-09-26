package com.example

import com.example.data.model.CuratedCases
import com.example.data.model.DistressLevel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testCuratedCasesCountAndIds() {
        val cases = CuratedCases.cases
        assertTrue(cases.size >= 7)

        val ids = cases.map { it.id }
        assertTrue(ids.contains("marcus_vance"))
        assertTrue(ids.contains("elena_rostova"))
        assertTrue(ids.contains("mateo_alvarez"))
        assertTrue(ids.contains("dr_arthur_pendelton"))
        assertTrue(ids.contains("priya_patel"))
        assertTrue(ids.contains("jordan_taylor"))
        assertTrue(ids.contains("tom_curb_mentor"))
    }

    @Test
    fun testCaseDossierIntegrity() {
        val marcus = CuratedCases.cases.find { it.id == "marcus_vance" }
        assertNotNull(marcus)
        assertEquals(42, marcus!!.age)
        assertTrue(marcus.dsm5Formulation.isNotBlank())
        assertTrue(marcus.activeDefenses.isNotEmpty())
        assertEquals(DistressLevel.MODERATE, marcus.baselineDistress)

        val elena = CuratedCases.cases.find { it.id == "elena_rostova" }
        assertNotNull(elena)
        assertTrue(elena!!.activeDefenses.any { it.name == "Intellectualization" })
    }

    @Test
    fun testLiteratureDatabase() {
        val literature = CuratedCases.literatureList
        assertTrue(literature.isNotEmpty())
        literature.forEach { item ->
            assertTrue(item.title.isNotBlank())
            assertTrue(item.keyFinding.isNotBlank())
            assertTrue(item.clinicalApplication.isNotBlank())
        }
    }
}
