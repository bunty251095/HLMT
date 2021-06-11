package com.caressa.hra.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.caressa.hra.ui.*


class HraQuestionsPagerAdapter internal constructor(fm: FragmentManager, isMale:Boolean, totalQuestions :Int)
    : FragmentPagerAdapter(fm) {

    private var isMale : Boolean = false
    private var totalQuestions = 27
    //private var registeredFragments = SparseArray<Fragment>()

    init {
        this.isMale = isMale
        this.totalQuestions = totalQuestions
    }

    override fun getItem(position: Int): Fragment {
        return if (isMale) {
            maleQuestions(position)
        } else {
            femaleQuestions(position)
        }
    }

    private fun maleQuestions( position: Int ) : Fragment {
        var fragment: Fragment? = null

        when (position) {
            0 -> fragment = HraQuesBmiFragment("BMI")
            1 -> fragment = HraQuesBloodPressureFragment("KNWBPNUM")
            2 -> fragment = HraQuesMultipleSelectionFragment("KNWDIANUM")
            3 -> fragment = HraQuesBloodSugarInputFragment("SUGAR_INPUT")
            4 -> fragment = HraQuesMultipleSelectionFragment("KNWLIPNUM")
            5 -> fragment = HraQuesCholesterolInputFragment("LIPID_INPUT")

            6 -> fragment = HraQuesMultipleSelectionFragment("HHILL")
            7 -> fragment = HraQuesMultipleSelectionFragment("EDS")
            8 -> fragment = HraQuesMultipleSelectionFragment("FHIST")

            9 -> fragment = HraQuesSingleSelectionFragment("5FRUIT")
            10 -> fragment = HraQuesSingleSelectionFragment("FATFOOD")
            11 -> fragment = HraQuesSingleSelectionFragment("PHYEXER")
            12 -> fragment = HraQuesSingleSelectionFragment("PHYSLEEP")
            13 -> fragment = HraQuesSingleSelectionFragment("SMOKECNT")
            14 -> fragment = HraQuesSingleSelectionFragment("DRINKCNT")
            15 -> fragment = HraQuesSingleSelectionFragment("BALWF")
            16 -> fragment = HraQuesSingleSelectionFragment("SOCSYSTM")
            17 -> fragment = HraQuesSingleSelectionFragment("GENOVER")
            18 -> fragment = HraQuesSingleSelectionFragment("JPT")
            19 -> fragment = HraQuesSingleSelectionFragment("OCCSHIFT")
            20 -> fragment = HraQuesSingleSelectionFragment("OCCPCTIM")

            21 -> fragment = HraQuesExposeCheckupFragment("EXPOSE")

            22 -> fragment = HraQuesSingleSelectionFragment("PHYSTRES")
            23 -> fragment = HraQuesMultipleSelectionFragment("PHY")

            24 -> fragment = HraQuesExposeCheckupFragment("CHECKUP")

            25 -> fragment = HraLastPageFragment("FINISH")
        }

        return fragment!!
    }

    private fun femaleQuestions( position: Int ) : Fragment {
        var fragment: Fragment? = null

        when (position) {
            0 -> fragment = HraQuesBmiFragment("BMI")
            1 -> fragment = HraQuesBloodPressureFragment("KNWBPNUM")
            2 -> fragment = HraQuesMultipleSelectionFragment("KNWDIANUM")
            3 -> fragment = HraQuesBloodSugarInputFragment("SUGAR_INPUT")
            4 -> fragment = HraQuesMultipleSelectionFragment("KNWLIPNUM")
            5 -> fragment = HraQuesCholesterolInputFragment("LIPID_INPUT")

            6 -> fragment = HraQuesMultipleSelectionFragment("HHILL")
            7 -> fragment = HraQuesMultipleSelectionFragment("WOMOTHER")
            8 -> fragment = HraQuesMultipleSelectionFragment("EDS")
            9 -> fragment = HraQuesMultipleSelectionFragment("FHIST")

            10 -> fragment = HraQuesSingleSelectionFragment("5FRUIT")
            11 -> fragment = HraQuesSingleSelectionFragment("FATFOOD")
            12 -> fragment = HraQuesSingleSelectionFragment("PHYEXER")
            13 -> fragment = HraQuesSingleSelectionFragment("PHYSLEEP")
            14 -> fragment = HraQuesSingleSelectionFragment("SMOKECNT")
            15 -> fragment = HraQuesSingleSelectionFragment("DRINKCNT")
            16 -> fragment = HraQuesSingleSelectionFragment("BALWF")
            17 -> fragment = HraQuesSingleSelectionFragment("SOCSYSTM")
            18 -> fragment = HraQuesSingleSelectionFragment("GENOVER")
            19 -> fragment = HraQuesSingleSelectionFragment("JPT")
            20 -> fragment = HraQuesSingleSelectionFragment("OCCSHIFT")
            21 -> fragment = HraQuesSingleSelectionFragment("OCCPCTIM")

            22 -> fragment = HraQuesExposeCheckupFragment("EXPOSE")

            23 -> fragment = HraQuesSingleSelectionFragment("PHYSTRES")
            24 -> fragment = HraQuesMultipleSelectionFragment("PHY")

            25 -> fragment = HraQuesExposeCheckupFragment("CHECKUP")

            26 -> fragment = HraLastPageFragment("FINISH")
        }

        return fragment!!
    }

    override fun getCount(): Int {
        return totalQuestions
    }

    override fun getItemPosition(`object`: Any): Int {
        return super.getItemPosition(`object`)
        // return PagerAdapter.POSITION_UNCHANGED
    }

    override fun getPageTitle(position: Int): CharSequence {
        return "Tab " + (position + 1)
    }

    ///////////////////////////////

/*    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        //return super.instantiateItem(container, position)
        val fragment = super.instantiateItem(container, position) as Fragment
        registeredFragments.put(position, fragment)
        return fragment
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        //super.destroyItem(container, position, `object`)
        registeredFragments.remove(position)
        super.destroyItem(container, position, `object`)
    }

    fun getRegisteredFragment(position: Int): Fragment {
        return registeredFragments.get(position)
    }*/

}