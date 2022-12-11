package com.example.project_mobile.data

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Attributes (
	var ID : Int= 9999,
	var OMSCHRIJVING : String?= null,
	var STRAAT : String?= null,
	var HUISNUMMER : String?= null,
	var POSTCODE : Int= 0,
	var DISTRICT : String?= null,
	var DOELGROEP : String?= null,
	var LUIERTAFEL : String?= null,
	var LAT : String?= null,
	var LONG : String?= null,
	var INTEGRAAL_TOEGANKELIJK : String?= null,
	var x : Double= 0.0,
	var y : Double= 0.0
) : Parcelable