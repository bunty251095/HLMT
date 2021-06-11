package vivant.me.app.di

import com.caressa.blogs.di.featureBlogsModule
import com.caressa.home.di.featureHomeModule
import com.caressa.local.di.localModule
import com.caressa.repository.di.repositoryModule
import com.caressa.security.di.featureSecurityModule
import com.caressa.common.constants.Constants
import com.caressa.fitness_tracker.di.featureFitnessModule
import com.caressa.hra.di.featureHraModule
import com.caressa.medication_tracker.di.featureMedicationModule
import com.caressa.records_tracker.di.featureShrModule
import com.caressa.remote.di.createRemoteModule
import com.caressa.tools_calculators.di.featureToolsCalculatorsModule
import com.caressa.track_parameter.di.featureParameterModule


val appComponent = listOf(
    createRemoteModule(Constants.strAPIUrl),
    repositoryModule,
    featureHomeModule,
    localModule,
    featureSecurityModule,
    featureHraModule,
    featureShrModule,
    featureMedicationModule,
    featureFitnessModule,
    featureParameterModule,
    featureBlogsModule,
    featureToolsCalculatorsModule)