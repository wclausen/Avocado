package com.wclausen.avocado

import android.app.Application
import com.airbnb.mvrx.MvRx
import com.airbnb.mvrx.MvRxViewModelConfigFactory
import com.squareup.inject.assisted.dagger2.AssistedModule
import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@AssistedModule
@Module(includes = [AssistedInject_AppAssistedModule::class])
abstract class AppAssistedModule

@Module
class AndroidModule(val activity: MainActivity) {

    @Provides
    fun provideActivity(): MainActivity = activity
}

@Singleton
@Component(modules = [AppAssistedModule::class, AndroidModule::class])
interface AppComponent {

    fun inject(contactsFragment: ContactsFragment)
    fun inject(contactsFragment: MainActivity)
}

class AvocadoApplication : Application() {

    lateinit var component: AppComponent

    override fun onCreate() {
        super.onCreate()
        MvRx.viewModelConfigFactory = MvRxViewModelConfigFactory(this)
        INSTANCE = this
    }

    companion object {
        private lateinit var INSTANCE: AvocadoApplication

        fun injector() = INSTANCE.component
    }
}