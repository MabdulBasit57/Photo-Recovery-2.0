package com.recover.photo.ui.fragment

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.recover.photo.R

/**
 * Created by mabdulbasit on 1/25/2025.
 */
class ContactUsFragment : Fragment() {
    private val email: TextView? = null
    private val llContactNum: LinearLayout? = null
    private val llWeb: LinearLayout? = null
    private val llFiverr: LinearLayout? = null
    private val llFacebook: LinearLayout? = null
    private val llYoutube: LinearLayout? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_contact, null)
        return view
    }

    //method to get the right URL to use in the intent
    fun getFacebookPageURL(context: Context): String {
        val packageManager = context.packageManager


        try {
            val versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode
            if (versionCode >= 3002850) { //newer versions of fb app

                val ai = activity?.packageManager?.getApplicationInfo("com.facebook.katana", 0)
                val appStatus = ai?.enabled
                return if (appStatus == true) {
                    "fb://facewebmodal/f?href=" + FACEBOOK_URL
                } else {
                    FACEBOOK_URL //normal web url
                }
            } else { //older versions of fb app
                val ai = activity?.packageManager?.getApplicationInfo("com.facebook.katana", 0)
                val appStatus = ai?.enabled
                return if (appStatus == true) {
                    "fb://page/" + FACEBOOK_PAGE_ID
                } else {
                    FACEBOOK_URL //normal web url
                }
            }
        } catch (e: PackageManager.NameNotFoundException) {
            return FACEBOOK_URL //normal web url
        }
    }


    private fun listeners() {
        /*
        llContactNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:+923001234567"));
                startActivity(intent);
            }
        });


        llWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.studeregateway.com"));
                startActivity(browserIntent);
            }
        });
        llFiverr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.fiverr.com/h_mateen"));
                startActivity(browserIntent);
            }
        });

        llFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
                String facebookUrl = getFacebookPageURL(getActivity());
                facebookIntent.setData(Uri.parse(facebookUrl));
                startActivity(facebookIntent);
            }
        });
        llYoutube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.youtube.com/channel/UCAyRERoIt5A4WEzjBwQfY5A"));
                startActivity(intent);
            }
        });
        */
    }

    private fun init(view: View) {
        //llContactNum = view.findViewById(R.id.llContactNum);
        // email = view.findViewById(R.id.email);
        // llWeb = view.findViewById(R.id.llWeb);
        // llFiverr = view.findViewById(R.id.llFiverr);
        // llFacebook = view.findViewById(R.id.llFacebook);
        // llYoutube = view.findViewById(R.id.llYoutube);
    }

    companion object {
        var FACEBOOK_URL: String = "https://www.facebook.com/mabdulbasit"
        var FACEBOOK_PAGE_ID: String = "0000"
    }
}
