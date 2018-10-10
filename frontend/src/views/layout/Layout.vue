<template lang="pug">
.app-wrapper(:class='classObj')
  .drawer-bg(v-if="device==='mobile'&&sidebar.opened" @click='handleClickOutside')
  sidebar.sidebar-container
  .main-container
    navbar
    tags-view
    app-main
</template>

<script>
import { Navbar, Sidebar, AppMain, TagsView } from './components'
import ResizeMixin from './mixin/ResizeHandler'

export default {
  name: 'layout',
  components: {
    Navbar,
    Sidebar,
    AppMain,
    TagsView
  },
  mixins: [ResizeMixin],
  computed: {
    sidebar() {
      return this.$store.state.app.sidebar
    },
    device() {
      return this.$store.state.app.device
    },
    classObj() {
      return {
        hideSidebar: !this.sidebar.opened,
        withoutAnimation: this.sidebar.withoutAnimation,
        mobile: this.device === 'mobile'
      }
    }
  },
  methods: {
    handleClickOutside() {
      this.$store.dispatch('closeSideBar', { withoutAnimation: false })
    }
  }
  // ,
  // mounted() {
  //   window.onscroll = function() { scroll() }

  //   const topbar = document.getElementById('topbar')
  //   const sticky = topbar.offsetTop
  //   function scroll() {
  //     if (window.pageYOffset > sticky) {
  //       topbar.classList.add('sticky')
  //     } else {
  //       topbar.classList.remove('sticky')
  //     }
  //   }
  // }
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
  @import "src/styles/mixin.scss";
  .app-wrapper {
    @include clearfix;
    position: relative;
    height: 100%;
    width: 100%;
  }
  .drawer-bg {
    background: #000;
    opacity: 0.3;
    width: 100%;
    top: 0;
    height: 100%;
    position: absolute;
    z-index: 999;
  }
  .main-container {
    height: 100%;
    min-height: unset;
  }


</style>
