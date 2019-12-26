package com.wclausen.avocado.di

import com.squareup.inject.assisted.dagger2.AssistedModule
import com.wclausen.avocado.MainActivity
import com.wclausen.avocado.addcontact.ContactsFragment
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
