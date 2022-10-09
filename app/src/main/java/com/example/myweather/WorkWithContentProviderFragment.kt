package com.example.myweather

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.myweather.databinding.FragmentWorkWithContentProviderBinding
import kotlinx.coroutines.*
import java.lang.Thread.sleep


class WorkWithContentProviderFragment() : Fragment()  {

    private var _binding: FragmentWorkWithContentProviderBinding? = null
    private val binding: FragmentWorkWithContentProviderBinding
        get() {
            return _binding!!
        }



    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkWithContentProviderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkPermission()
    }

    private fun checkPermission(){
        if(ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.READ_CONTACTS)==PackageManager.PERMISSION_GRANTED){
            binding.loadingLayout.visibility = View.VISIBLE
            Thread{
                renderData()
            }.start()
        }else if (shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)){
            explain()
        }else {
            requestPermission()
        }
    }

    private fun explain(){
        AlertDialog.Builder(requireContext())
            .setTitle("Уверены?")
            .setMessage("Если не разрешить ничего не получим")
            .setPositiveButton("Выдать разрешение"){_, _ ->
                requestPermission()
            }
            .setNegativeButton("Не надо"){dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun requestPermission() {
        requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS),999)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 999){
            for(i in permissions.indices){
                if(permissions[i]==Manifest.permission.READ_CONTACTS&&grantResults[i]==PackageManager.PERMISSION_GRANTED){
                    renderData()
                }else{
                    explain()
                }
            }
        }else{
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }

    }

    lateinit var myContacts:ArrayList<Contact>

    private suspend fun contacts() = coroutineScope {
        val contacts = async{
           return@async getContacts()
        }
    }


    @SuppressLint("Range")
    fun getContacts():ArrayList<Contact> {
           val contacts = ArrayList<Contact>()
           val cr = requireContext().contentResolver

           sleep(1000L)
           val pCur = cr.query(
               ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
               arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.CONTACT_ID),
               null,
               null,
               null
           )
           if (pCur != null) {
               if (pCur.count > 0) {
                   val phones: HashMap<Int, ArrayList<String>> = HashMap()
                   while (pCur.moveToNext()) {
                       val contactId: Int = pCur.getInt(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID))
                       var curPhones: ArrayList<String> = ArrayList()
                       if (phones.containsKey(contactId)) {
                           curPhones = phones[contactId]!!
                       }
                       curPhones.add(pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)))
                       phones[contactId] = curPhones
                   }
                   val cur = cr.query(
                       ContactsContract.Contacts.CONTENT_URI,
                       arrayOf(ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts.HAS_PHONE_NUMBER),
                       ContactsContract.Contacts.HAS_PHONE_NUMBER + " > 0",
                       null,
                       ContactsContract.Contacts.DISPLAY_NAME + " ASC"
                   )
                   if (cur != null) {
                       if (cur.count > 0) {

                           while (cur.moveToNext()) {
                               val id: Int = cur.getInt(cur.getColumnIndex(ContactsContract.Contacts._ID))
                               if (phones.containsKey(id)) {
                                   val con = Contact()
                                   con.setMyId(id)
                                   con.setName(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)))
                                   con.setPhone(phones[id]!![0])
                                   contacts.add(con)
                               }
                           }
                       }
                       cur.close()
                   }
               }
               pCur.close()
           }
        return contacts
    }

    inner class Contact {
        var name = ""
        var phone = ""
        private var my_id = 0
        @JvmName("setName1")
        fun setName(s: String) {
            name = s
        }

        @JvmName("setPhone1")
        fun setPhone(s: String) {
            phone = s
        }

        fun setMyId(i: Int) {
            my_id = i
        }
    }

    private fun renderData(){
        var contacts:ArrayList<Contact> = arrayListOf()
        val mThread = Thread{
            contacts = getContacts()
        }
        mThread.start()
        mThread.join()
        requireActivity().runOnUiThread{binding.loadingLayout.visibility = View.GONE}
        for (i in contacts.indices){
            requireActivity().runOnUiThread{
                binding.contactsContainer.addView(TextView(requireContext()).apply {
                    text = contacts[i].name
                    textSize = 26f
                    setTextColor(Color.BLACK)
                    gravity = Gravity.CENTER
                })
            }
            requireActivity().runOnUiThread{
                binding.contactsContainer.addView(TextView(requireContext()).apply {
                    text = contacts[i].phone
                    textSize = 18f
                    setTextColor(Color.BLACK)
                    setOnClickListener{
                        makeCall(contacts[i].phone)
                    }
                })
            }
        }
    }

    private fun makeCall(phone: String){

        val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null))
        startActivity(intent)
    }

    companion object {

        fun newInstance() = WorkWithContentProviderFragment()
    }

}