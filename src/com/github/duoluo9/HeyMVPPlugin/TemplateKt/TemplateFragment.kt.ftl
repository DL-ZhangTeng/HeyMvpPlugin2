package $packagename

import $basepackagename.BaseMvpFragment


/**
 * @author $author
 * Create at @date $date
 */
class $nameFragment : BaseMvpFragment<$namePresenter>() {
    override fun layoutId(): Int {
        return 0
    }

    override fun getPresenter(): $namePresenter {
        return $namePresenter()
    }

    override fun initView() {

    }

    override fun initData() {
    }

}
