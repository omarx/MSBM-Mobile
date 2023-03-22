package com.fras.msbm.events.clicks;

import com.fras.msbm.models.directory.Contact;

/**
 * Created by Shane on 7/7/2016.
 */

public class ContactDetailEvent {
    private final Contact contact;

    public ContactDetailEvent(Contact contact) {
        this.contact = contact;
    }

    public Contact getContact() {
        return contact;
    }
}
