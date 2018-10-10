<template lang="pug">
div(style="display:flex;")
  span.connection(v-if="connection") @{{connection ? connection.name : ''}}
  el-breadcrumb.app-breadcrumb(separator='/')
    transition-group(name='breadcrumb')
      el-breadcrumb-item(v-for='(item,index)  in levelList' :key='item.path' v-if='item.meta.title')
        span.no-redirect(v-if='item.redirect==="noredirect"||index==levelList.length-1') {{generateTitle(item.meta.title)}}
        router-link(v-else='' :to='item.redirect||item.path') {{generateTitle(item.meta.title)}}
</template>

<script>
import { mapGetters } from 'vuex'
import { generateTitle } from '@/utils/i18n'

export default {
  created() {
    this.getBreadcrumb()
  },
  data() {
    return {
      levelList: null
    }
  },
  watch: {
    $route() {
      this.getBreadcrumb()
    }
  },
  computed: {
    ...mapGetters(['connection'])
  },
  methods: {
    generateTitle,
    getBreadcrumb() {
      let matched = this.$route.matched.filter(item => item.name)
      const first = matched[0]
      if (first && first.name !== 'dashboard') {
        matched = [{ path: '/dashboard', meta: { title: 'dashboard' }}].concat(matched)
      }
      this.levelList = matched
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
  .app-breadcrumb.el-breadcrumb {
    display: inline-block;
    font-size: 14px;
    line-height: 50px;
    margin-left: 10px;
    .no-redirect {
      color: #97a8be;
      cursor: text;
    }
  }
  .connection {
    margin-right: 10px;
    color: #5D4037 ;
    font-weight: bolder ;
  }
</style>
