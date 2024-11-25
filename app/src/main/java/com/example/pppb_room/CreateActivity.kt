package com.example.pppb_room

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pppb_room.Database.DataPemilih
import com.example.pppb_room.Database.DataPemilihDao
import com.example.pppb_room.Database.DataPemilihDatabase
import com.example.pppb_room.MainActivity
import com.example.pppb_room.databinding.ActivityCreateBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CreateActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateBinding
    private lateinit var executorService: ExecutorService
    private lateinit var dataPemilihDao : DataPemilihDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        executorService = Executors.newSingleThreadExecutor()
        val db = DataPemilihDatabase.getDatabase(this)
        dataPemilihDao = db!!.dataPemilihDao()
        with(binding){
            btnSimpan.setOnClickListener {
                val selectedGenderId = binding.rgGender.checkedRadioButtonId
                val gender = when (selectedGenderId) {
                    binding.rbMale.id -> "Laki-laki"
                    binding.rbFemale.id -> "Perempuan"
                    else -> "Tidak ada yang dipilih"
                }
                // Validasi Input
                if (etNamaPemilih.text.isNullOrBlank() ||
                    etNik.text.isNullOrBlank() ||
                    gender == "Tidak ada yang dipilih" ||
                    etAlamat.text.isNullOrBlank()
                ) {
                    // Jika data tidak valid
                    showToast("Semua data harus diisi dengan benar!")
                    return@setOnClickListener
                }
                insert(
                    DataPemilih(
                        nama = etNamaPemilih.text.toString(),
                        nik = etNik.text.toString(),
                        jenisKelamin = gender,
                        alamat = etAlamat.text.toString(),
                    )
                )
                showToast("Data berhasil disimpan!")
                setEmptyField()
                val startActivity = Intent(this@CreateActivity, MainActivity::class.java)
                startActivity(startActivity)
                finish()
            }
        }
    }
    private fun insert(dataPemilih: DataPemilih){
        executorService.execute {
            dataPemilihDao.insert(dataPemilih)
            runOnUiThread {
                val intent = Intent(this@CreateActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
    private fun showToast(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
    private fun setEmptyField() {
        with(binding) {
            etNamaPemilih.text?.clear()
            etNik.text?.clear()
            rgGender.clearCheck()
            etAlamat.text?.clear()
        }
    }
}