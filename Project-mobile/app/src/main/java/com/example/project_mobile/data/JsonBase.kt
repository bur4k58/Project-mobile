package com.example.project_mobile.data


data class JsonBase (

	val displayFieldName : String,
	val fieldAliases : FieldAliases,
	val geometryType : String,
	val spatialReference : SpatialReference,
	val fields : List<Fields>,
	val features : List<Features>
)