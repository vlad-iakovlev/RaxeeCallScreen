package ru.raxee.call_screen;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;

class Contact {
    private Context context;

    enum Number {HIDDEN, JUST_PHONE, FULL}
    Number type = Number.HIDDEN;

    String number = null;
    String name = null;
    String company = null;
    String companyPosition = null;
    Bitmap photo = null;


    Contact(String phoneNumber) {
        context = App.getContext();

        try {
            setNumber(phoneNumber);
            type = Number.JUST_PHONE;

            setContact(phoneNumber);
            type = Number.FULL;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setNumber(String phoneNumber) throws Exception {
        int numberInt = 0;
        try {
            numberInt = Integer.parseInt(phoneNumber);
        } catch (Exception ignored) {}

        if (numberInt <= 0) {
            throw new Exception("Hidden number");
        }

        number = PhoneNumberUtils.formatNumber(phoneNumber, "US");
        if (number == null) {
            number = phoneNumber;
        }
    }

    private void setContact(String phoneNumber) {
        ContentResolver contentResolver = context.getContentResolver();


        // Получаем URI номера
        Uri phoneUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));

        // Получаем контакт по номеру
        Cursor contactCursor = contentResolver.query(phoneUri, null, null, null, null);
        assert contactCursor != null;
        contactCursor.moveToFirst();

        // Получаем ID контакта для запроса дополнительных данных
        String contactId = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.Contacts._ID));

        // Сохраняем имя
        name = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

        contactCursor.close();


        // Получаем организацию по ID контакта
        Cursor orgCursor = contentResolver.query(
                ContactsContract.Data.CONTENT_URI,
                null,
                ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?",
                new String[]{
                        contactId,
                        ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE
                },
                null
        );
        assert orgCursor != null;
        if (orgCursor.moveToFirst()) {
            // Получаем компанию и должность
            company = orgCursor.getString(orgCursor.getColumnIndex(ContactsContract.CommonDataKinds.Organization.COMPANY));
            companyPosition = orgCursor.getString(orgCursor.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TITLE));
        }

        orgCursor.close();


        // Получаем URI контакта
        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(contactId));

        // Получаем URI фото
        Uri displayPhotoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.DISPLAY_PHOTO);
        try {
            AssetFileDescriptor photoFd = contentResolver.openAssetFileDescriptor(displayPhotoUri, "r");
            assert photoFd != null;
            photo = BitmapFactory.decodeStream(photoFd.createInputStream());
        } catch (Exception e) {
            photo = null;
        }
    }
}
