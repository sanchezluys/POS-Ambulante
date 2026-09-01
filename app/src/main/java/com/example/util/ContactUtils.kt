package com.example.util

import android.content.Context
import android.net.Uri
import android.provider.ContactsContract
import android.util.Log

data class ContactInfo(
  val name: String,
  val phone: String
)

object ContactUtils {
  private const val TAG = "ContactUtils"

  /**
   * Extracts name and phone number from a picked contact URI
   */
  fun getContactDetails(context: Context, contactUri: Uri): ContactInfo? {
    try {
      val contentResolver = context.contentResolver
      val cursor = contentResolver.query(
        contactUri,
        arrayOf(
          ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
          ContactsContract.CommonDataKinds.Phone.NUMBER
        ),
        null,
        null,
        null
      )

      cursor?.use {
        if (it.moveToFirst()) {
          val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
          val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

          val name = if (nameIndex != -1) it.getString(nameIndex) ?: "" else ""
          val number = if (numberIndex != -1) it.getString(numberIndex) ?: "" else ""

          return ContactInfo(
            name = name.trim(),
            phone = cleanPhoneNumber(number)
          )
        }
      }
    } catch (e: Exception) {
      Log.e(TAG, "Error retrieving contact details", e)
    }
    return null
  }

  /**
   * Cleans contact phone number removing non-numeric characters except leading +
   */
  fun cleanPhoneNumber(rawPhone: String): String {
    val trimmed = rawPhone.trim()
    val hasPlus = trimmed.startsWith("+")
    val digitsOnly = trimmed.replace(Regex("[^0-9]"), "")
    return if (hasPlus && digitsOnly.isNotEmpty()) {
      "+$digitsOnly"
    } else {
      digitsOnly
    }
  }
}
