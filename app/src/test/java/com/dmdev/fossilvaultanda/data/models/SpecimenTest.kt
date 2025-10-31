package com.dmdev.fossilvaultanda.data.models

import com.dmdev.fossilvaultanda.data.models.enums.*
import com.fossilVault.geological.*
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import kotlin.time.Duration.Companion.days

/**
 * Unit tests for Specimen model
 * Adapted from iOS SpecimenTests.swift following Android/Kotlin conventions
 */
class SpecimenTest {

    // MARK: - Test Data

    private val sampleId = "specimen_123"
    private val sampleUserId = "user_456"
    private val sampleLocation = "Green River Formation, Wyoming"
    private val sampleCountry = "US"
    private val sampleFormation = "Green River Formation"
    private val sampleNotes = "Well-preserved specimen with excellent detail"
    private val sampleInventoryId = "INV-2023-001"
    private val sampleShareUrl = "https://example.com/share/specimen_123"
    private val sampleCreationDate = Instant.parse("2023-09-11T12:00:00Z")
    private val sampleCollectionDate = Instant.parse("2023-01-01T00:00:00Z")
    private val sampleAcquisitionDate = Instant.parse("2023-04-01T00:00:00Z")
    private val sampleDispositionDate = Instant.parse("2023-04-01T00:00:00Z")

    private lateinit var sampleTaxonomy: Taxonomy
    private lateinit var sampleGeologicalTime: GeologicalTime
    private lateinit var sampleStoredImage: StoredImage
    private lateinit var sampleStorageMethod: StorageMethod
    private lateinit var sampleDisposition: Disposition

    @Before
    fun setup() {
        sampleTaxonomy = Taxonomy(
            kingdom = "Animalia",
            phylum = "Arthropoda",
            subPhylum = null,
            taxonomicClass = "Trilobita",
            subClass = null,
            superOrder = null,
            order = "Phacopida",
            infraOrder = null,
            subOrder = null,
            superFamily = null,
            family = "Phacopidae",
            genus = "Phacops",
            subGenus = null,
            species = "Phacops rana"
        )

        sampleGeologicalTime = GeologicalTime(
            era = GeologicalEra.PALEOZOIC,
            period = GeologicalPeriod.DEVONIAN,
            epoch = GeologicalEpoch.MIDDLE_DEVONIAN,
            age = GeologicalAge.EIFELIAN
        )

        sampleStoredImage = StoredImage(
            url = "https://example.com/specimen.jpg",
            path = "/path/to/specimen.jpg",
            size = 1024,
            format = ImageFormat.JPG
        )

        sampleStorageMethod = StorageMethod(
            drawer = "Drawer 3",
            cabinet = "Cabinet 5",
            room = "Collection Room A"
        )

        sampleDisposition = Disposition(
            status = OwnershipStatus.SOLD,
            date = sampleDispositionDate,
            recipient = "John Smith",
            price = Price(amount = 123.45, currency = Currency.EUR),
            notes = "Here are the notes",
            imageURLs = listOf("https://foo.com")
        )
    }

    // MARK: - Validation Tests

    @Test
    fun testValidateSuccess() {
        val specimen = createCompleteSpecimen()
        val result = specimen.validate()
        assertTrue("Validation should succeed for complete specimen", result.isSuccess)
    }

    @Test
    fun testValidateBlankId() {
        val specimen = createMinimalSpecimen().copy(id = "")
        val result = specimen.validate()
        assertTrue("Validation should fail for blank ID", result.isFailure)
        assertTrue(
            "Error message should mention ID",
            result.exceptionOrNull()?.message?.contains("ID") == true
        )
    }

    @Test
    fun testValidateBlankUserId() {
        val specimen = createMinimalSpecimen().copy(userId = "")
        val result = specimen.validate()
        assertTrue("Validation should fail for blank user ID", result.isFailure)
    }

    @Test
    fun testValidateBlankSpecies() {
        val specimen = createMinimalSpecimen().copy(taxonomy = Taxonomy(species = ""))
        val result = specimen.validate()
        assertTrue("Validation should fail for blank species", result.isFailure)
    }

    @Test
    fun testValidateInvalidLatitude() {
        val specimen = createMinimalSpecimen().copy(latitude = -91.0)
        val result = specimen.validate()
        assertTrue("Validation should fail for latitude < -90", result.isFailure)

        val specimen2 = createMinimalSpecimen().copy(latitude = 91.0)
        val result2 = specimen2.validate()
        assertTrue("Validation should fail for latitude > 90", result2.isFailure)
    }

    @Test
    fun testValidateInvalidLongitude() {
        val specimen = createMinimalSpecimen().copy(longitude = -181.0)
        val result = specimen.validate()
        assertTrue("Validation should fail for longitude < -180", result.isFailure)

        val specimen2 = createMinimalSpecimen().copy(longitude = 181.0)
        val result2 = specimen2.validate()
        assertTrue("Validation should fail for longitude > 180", result2.isFailure)
    }

    @Test
    fun testValidateNegativeWeight() {
        val specimen = createMinimalSpecimen().copy(weight = -10.0)
        val result = specimen.validate()
        assertTrue("Validation should fail for negative weight", result.isFailure)
        assertTrue(
            "Error message should mention weight",
            result.exceptionOrNull()?.message?.contains("Weight") == true
        )
    }

    @Test
    fun testValidateNegativeDimensions() {
        val specimen1 = createMinimalSpecimen().copy(width = -5.0)
        assertTrue("Validation should fail for negative width", specimen1.validate().isFailure)

        val specimen2 = createMinimalSpecimen().copy(height = -5.0)
        assertTrue("Validation should fail for negative height", specimen2.validate().isFailure)

        val specimen3 = createMinimalSpecimen().copy(length = -5.0)
        assertTrue("Validation should fail for negative length", specimen3.validate().isFailure)
    }

    @Test
    fun testValidateNegativePrice() {
        val specimen = createMinimalSpecimen().copy(pricePaid = -100.0)
        val result = specimen.validate()
        assertTrue("Validation should fail for negative price paid", result.isFailure)
    }

    @Test
    fun testValidateNegativeEstimatedValue() {
        val specimen = createMinimalSpecimen().copy(estimatedValue = -100.0)
        val result = specimen.validate()
        assertTrue("Validation should fail for negative estimated value", result.isFailure)
    }

    // MARK: - Computed Properties Tests

    @Test
    fun testDimensionsDescriptionWithAllDimensions() {
        val specimen = createMinimalSpecimen().copy(
            length = 67.2,
            width = 45.5,
            height = 23.8,
            unit = SizeUnit.MM
        )

        val description = specimen.dimensionsDescription
        assertNotNull("Dimensions description should not be null", description)
        assertEquals("Dimensions should be formatted as LxWxH unit", "67.2x45.5x23.8 mm", description)
    }

    @Test
    fun testDimensionsDescriptionWithPartialDimensions() {
        val specimen = createMinimalSpecimen().copy(
            length = 67.2,
            width = 45.5,
            unit = SizeUnit.CM
        )

        val description = specimen.dimensionsDescription
        assertNotNull("Dimensions description should not be null", description)
        assertEquals("Dimensions should be formatted correctly", "67.2x45.5 cm", description)
    }

    @Test
    fun testDimensionsDescriptionWithNoDimensions() {
        val specimen = createMinimalSpecimen()
        val description = specimen.dimensionsDescription
        assertNull("Dimensions description should be null when no dimensions are set", description)
    }

    @Test
    fun testIsArchivedWithDisposition() {
        val specimen = createMinimalSpecimen().copy(disposition = sampleDisposition)
        assertTrue("Specimen should be archived when disposition is set", specimen.isArchived)
    }

    @Test
    fun testIsArchivedWithoutDisposition() {
        val specimen = createMinimalSpecimen()
        assertFalse("Specimen should not be archived when disposition is null", specimen.isArchived)
    }

    // MARK: - CSV Export Tests

    @Test
    fun testToCsvRowWithAllFields() {
        val specimen = createCompleteSpecimen()
        val csvRow = specimen.toCsvRow()

        assertNotNull("CSV row should not be null", csvRow)
        assertTrue("CSV row should contain specimen ID", csvRow.contains(sampleId))
        assertTrue("CSV row should contain species", csvRow.contains("Phacops rana"))
        assertTrue("CSV row should contain location", csvRow.contains(sampleLocation))
        assertTrue("CSV row should contain country", csvRow.contains(sampleCountry))
    }

    @Test
    fun testToCsvRowWithSpecialCharacters() {
        val specimen = createMinimalSpecimen().copy(
            notes = "Contains, commas and \"quotes\""
        )
        val csvRow = specimen.toCsvRow()

        assertNotNull("CSV row should not be null", csvRow)
        // Special characters should be escaped
        assertTrue("CSV row should escape quotes", csvRow.contains("\"\""))
    }

    @Test
    fun testCsvHeaderMatchesColumns() {
        val specimen = createCompleteSpecimen()
        val csvRow = specimen.toCsvRow()
        val headerColumns = Specimen.CSV_HEADER.split(",")
        val rowColumns = csvRow.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)".toRegex())

        assertEquals(
            "Number of CSV header columns should match row columns",
            headerColumns.size,
            rowColumns.size
        )
    }

    // MARK: - Firestore Conversion Tests

    @Test
    fun testToFirestoreMapComplete() {
        val specimen = createCompleteSpecimen()
        val firestoreMap = specimen.toFirestoreMap()

        assertEquals("Firestore map should contain correct ID", sampleId, firestoreMap["id"])
        assertEquals("Firestore map should contain correct user ID", sampleUserId, firestoreMap["userId"])
        assertEquals("Firestore map should contain country", sampleCountry, firestoreMap["country"])
        assertNotNull("Firestore map should contain taxonomy", firestoreMap["taxonomy"])
        assertNotNull("Firestore map should contain geological time", firestoreMap["geologicalTime"])
        assertNotNull("Firestore map should contain storage", firestoreMap["storage"])
        assertNotNull("Firestore map should contain disposition", firestoreMap["disposition"])
        assertEquals("Firestore map should contain share URL", sampleShareUrl, firestoreMap["shareUrl"])
    }

    @Test
    fun testToFirestoreMapWithEnums() {
        val specimen = createCompleteSpecimen()
        val firestoreMap = specimen.toFirestoreMap()

        assertEquals("Element should be serialized", "ammonite", firestoreMap["element"])
        assertEquals("Unit should be serialized", "mm", firestoreMap["unit"])
        assertEquals("Weight unit should be serialized", "gr", firestoreMap["weightUnit"])
        assertEquals("Acquisition method should be serialized", "found", firestoreMap["acquisitionMethod"])
        assertEquals("Condition should be serialized", "restored", firestoreMap["condition"])
    }

    @Test
    fun testToFirestoreMapWithStorage() {
        val specimen = createMinimalSpecimen().copy(storage = sampleStorageMethod)
        val firestoreMap = specimen.toFirestoreMap()

        @Suppress("UNCHECKED_CAST")
        val storageMap = firestoreMap["storage"] as? Map<String, Any?>
        assertNotNull("Storage map should not be null", storageMap)
        assertEquals("Storage room should match", "Collection Room A", storageMap?.get("room"))
        assertEquals("Storage cabinet should match", "Cabinet 5", storageMap?.get("cabinet"))
        assertEquals("Storage drawer should match", "Drawer 3", storageMap?.get("drawer"))
    }

    @Test
    fun testToFirestoreMapWithDisposition() {
        val specimen = createMinimalSpecimen().copy(disposition = sampleDisposition)
        val firestoreMap = specimen.toFirestoreMap()

        @Suppress("UNCHECKED_CAST")
        val dispositionMap = firestoreMap["disposition"] as? Map<String, Any?>
        assertNotNull("Disposition map should not be null", dispositionMap)
        assertEquals("Disposition status should match", "sold", dispositionMap?.get("status"))
        assertEquals("Disposition recipient should match", "John Smith", dispositionMap?.get("recipient"))
        assertNotNull("Disposition price should not be null", dispositionMap?.get("price"))
    }

    // MARK: - Equality Tests

    @Test
    fun testEqualsWithSameData() {
        val specimen1 = createCompleteSpecimen()
        val specimen2 = createCompleteSpecimen()

        assertEquals("Specimens with same data should be equal", specimen1, specimen2)
    }

    @Test
    fun testEqualsWithDifferentData() {
        val specimen1 = createCompleteSpecimen()
        val specimen2 = createCompleteSpecimen().copy(id = "different_id")

        assertNotEquals("Specimens with different data should not be equal", specimen1, specimen2)
    }

    @Test
    fun testHashCodeConsistency() {
        val specimen1 = createCompleteSpecimen()
        val specimen2 = createCompleteSpecimen()

        assertEquals("Hash codes should be equal for equal specimens", specimen1.hashCode(), specimen2.hashCode())
    }

    // MARK: - Helper Methods

    private fun createMinimalSpecimen(): Specimen {
        return Specimen(
            id = sampleId,
            userId = sampleUserId,
            taxonomy = Taxonomy(species = "Unknown species"),
            geologicalTime = GeologicalTime(),
            element = FossilElement.OTHER,
            unit = SizeUnit.MM,
            weightUnit = WeightUnit.GR,
            creationDate = sampleCreationDate,
            imageUrls = emptyList(),
            isFavorite = false,
            tagNames = emptyList(),
            isPublic = false
        )
    }

    private fun createCompleteSpecimen(): Specimen {
        return Specimen(
            id = sampleId,
            userId = sampleUserId,
            taxonomy = sampleTaxonomy,
            geologicalTime = sampleGeologicalTime,
            element = FossilElement.AMMONITE,
            location = sampleLocation,
            country = sampleCountry,
            formation = sampleFormation,
            latitude = 41.2565,
            longitude = -110.1428,
            width = 45.5,
            height = 23.8,
            length = 67.2,
            unit = SizeUnit.MM,
            weight = 125.6,
            weightUnit = WeightUnit.GR,
            collectionDate = sampleCollectionDate,
            acquisitionDate = sampleAcquisitionDate,
            creationDate = sampleCreationDate,
            acquisitionMethod = AcquisitionMethod.FOUND,
            condition = Condition.RESTORED,
            inventoryId = sampleInventoryId,
            notes = sampleNotes,
            storage = sampleStorageMethod,
            imageUrls = listOf(sampleStoredImage),
            shareUrl = sampleShareUrl,
            isFavorite = true,
            tagNames = listOf("trilobite", "devonian", "complete"),
            isPublic = true,
            pricePaid = 150.00,
            pricePaidCurrency = Currency.USD,
            estimatedValue = 200.00,
            estimatedValueCurrency = Currency.USD,
            disposition = sampleDisposition
        )
    }
}
