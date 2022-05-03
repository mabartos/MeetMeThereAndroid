package org.mabartos.meetmethere

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import com.nostra13.universalimageloader.utils.StorageUtils
import org.mabartos.meetmethere.databinding.ActivityMainBinding
import java.io.File


class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val cacheDir: File = StorageUtils.getCacheDirectory(this)
        val config = ImageLoaderConfiguration.Builder(this)
            .memoryCache(LruMemoryCache(2 * 1024 * 1024))
            .memoryCacheSize(2 * 1024 * 1024)
            .diskCacheSize(50 * 1024 * 1024)
            .diskCache(UnlimitedDiskCache(cacheDir))
            .diskCacheFileCount(100).build()

        ImageLoader.getInstance().init(config)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigation.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomNavigation.isVisible = false
            //binding.bottomNavigation.isVisible = destination.id == R.id.listFragment
        }
    }
}