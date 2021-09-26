package $packagename

import $basepackagename.BaseMvpActivity


/**
 * @author $author
 * Create at @date $date
 */
class $nameActivity : BaseMvpActivity<$namePresenter>() {
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
