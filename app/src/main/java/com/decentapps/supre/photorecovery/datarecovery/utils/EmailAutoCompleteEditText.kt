package com.decentapps.supre.photorecovery.datarecovery.utils

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatAutoCompleteTextView

class EmailAutoCompleteEditText : AppCompatAutoCompleteTextView {
    var domains: Array<String> = arrayOf(
        "aol.com",
        "att.net",
        "comcast.net",
        "facebook.com",
        "gmail.com",
        "gmx.com",
        "googlemail.com",
        "google.com",
        "hotmail.com",
        "hotmail.co.uk",
        "mac.com",
        "me.com",
        "mail.com",
        "msn.com",
        "live.com",
        "sbcglobal.net",
        "verizon.net",
        "yahoo.com",
        "yahoo.co.uk",
        "email.com",
        "games.com",
        "gmx.net",
        "hush.com",
        "hushmail.com",
        "inbox.com",
        "lavabit.com",
        "love.com",
        "pobox.com",
        "rocketmail.com",
        "safe-mail.net",
        "wow.com",
        "ygm.com",
        "ymail.com",
        "zoho.com",
        "fastmail.fm",
        "bellsouth.net",
        "charter.net",
        "cox.net",
        "earthlink.net",
        "juno.com",
        "btinternet.com",
        "virginmedia.com",
        "blueyonder.co.uk",
        "freeserve.co.uk",
        "live.co.uk",
        "ntlworld.com",
        "o2.co.uk",
        "orange.net",
        "sky.com",
        "talktalk.co.uk",
        "tiscali.co.uk",
        "virgin.net",
        "wanadoo.co.uk",
        "bt.com",
        "sina.com",
        "qq.com",
        "naver.com",
        "hanmail.net",
        "daum.net",
        "nate.com",
        "yahoo.co.jp",
        "yahoo.co.kr",
        "yahoo.co.id",
        "yahoo.co.in",
        "yahoo.com.sg",
        "yahoo.com.ph",
        "hotmail.fr",
        "live.fr",
        "laposte.net",
        "yahoo.fr",
        "wanadoo.fr",
        "orange.fr",
        "gmx.fr",
        "sfr.fr",
        "neuf.fr",
        "free.fr",
        "gmx.de",
        "hotmail.de",
        "live.de",
        "online.de",
        "t-online.de",
        "web.de",
        "yahoo.de",
        "mail.ru",
        "rambler.ru",
        "yandex.ru",
        "hotmail.be",
        "live.be",
        "skynet.be",
        "voo.be",
        "tvcablenet.be",
        "hotmail.com.ar",
        "live.com.ar",
        "yahoo.com.ar",
        "fibertel.com.ar",
        "speedy.com.ar",
        "arnet.com.ar",
        "hotmail.com",
        "gmail.com",
        "yahoo.com.mx",
        "live.com.mx",
        "yahoo.com",
        "hotmail.es",
        "live.com",
        "hotmail.com.mx",
        "prodigy.net.mx",
        "msn.com"
    )

    constructor(context: Context?, attributeSet: AttributeSet?, i: Int) : super(
        context!!, attributeSet, i
    ) {
        init()
    }

    constructor(context: Context?, attributeSet: AttributeSet?) : super(
        context!!, attributeSet
    ) {
        init()
    }

    constructor(context: Context?) : super(context!!) {
        init()
    }

    fun init() {
        inputType = 32
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i2: Int, i3: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i2: Int, i3: Int) {
            }

            override fun afterTextChanged(editable: Editable) {
            }
        })
    }
}
