package com.tylerpalcic.onboarding_domain.di

import com.tylerpalcic.onboarding_domain.use_case.ValidateNutrients
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object OnBoardingDomainModule {

    @Provides
    fun providesValidateNutrients(): ValidateNutrients = ValidateNutrients()
}