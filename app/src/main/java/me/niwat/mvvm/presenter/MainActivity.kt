package me.niwat.mvvm.presenter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import me.niwat.mvvm.databinding.ActivityMainBinding
import me.niwat.mvvm.presenter.dex.PokedexFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction()
            .replace(binding.frameLayout.id, PokedexFragment.newInstance())
            .commit()
    }
}