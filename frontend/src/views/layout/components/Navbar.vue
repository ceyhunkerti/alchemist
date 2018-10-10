<template lang="pug">
  el-menu.navbar(mode='horizontal')
    hamburger.hamburger-container(:toggleClick='toggleSideBar' :isActive='sidebar.opened')
    breadcrumb.breadcrumb-container
    .right-menu(style="padding-top:15px;")
      connection-select.connection-select.right-menu-item
      el-tooltip(effect='dark' :content="$t('navbar.screenfull')" placement='bottom')
        screenfull.screenfull.right-menu-item
      lang-select.international.right-menu-item
      profile-menu.profile-menu.right-menu-item
</template>

<script>
import { mapGetters } from 'vuex'
import Breadcrumb from '@/components/Breadcrumb'
import Hamburger from '@/components/Hamburger'
import Screenfull from '@/components/Screenfull'
import LangSelect from '@/components/LangSelect'
import ConnectionSelect from '@/components/ConnectionSelect'
import ProfileMenu from '@/components/ProfileMenu'

export default {
  components: {
    ProfileMenu,
    ConnectionSelect,
    Breadcrumb,
    Hamburger,
    Screenfull,
    LangSelect
  },
  computed: {
    ...mapGetters([
      'sidebar',
      'name',
      'avatar'
    ])
  },
  methods: {
    toggleSideBar() {
      this.$store.dispatch('toggleSideBar')
    },
    logout() {
      this.$store.dispatch('LogOut').then(() => {
        location.reload()// In order to re-instantiate the vue-router object to avoid bugs
      })
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
.navbar {
  height: 50px;
  line-height: 50px;
  border-radius: 0px !important;
  .hamburger-container {
    line-height: 58px;
    height: 50px;
    float: left;
    padding: 0 10px;
  }
  .breadcrumb-container{
    float: left;
  }
  .errLog-container {
    display: inline-block;
    vertical-align: top;
  }
  .right-menu {
    float: right;
    height: 100%;
    &:focus{
     outline: none;
    }
    .right-menu-item {
      display: inline-block;
      margin: 0 8px;
    }
    .screenfull {
      vertical-align: top;
      height: 20px;
    }
    .international{
      line-height: 20px;
      vertical-align: top;
    }
    .theme-switch {
      vertical-align: 15px;
    }
    .avatar-container {
      height: 50px;
      margin-right: 30px;
      .avatar-wrapper {
        cursor: pointer;
        margin-top: 5px;
        position: relative;
        .user-avatar {
          width: 40px;
          height: 40px;
          border-radius: 10px;
        }
        .el-icon-caret-bottom {
          position: absolute;
          right: -20px;
          top: 25px;
          font-size: 12px;
        }
      }
    }
    .connection-select {
      height: 20px;
      line-height: 20px;
      vertical-align: top;
    }
    .profile-menu {
      height: 20px;
      line-height: 20px;
      vertical-align: top;
    }
  }
}
</style>

<style rel="stylesheet/scss" lang="scss" scoped>
// .navbar {
//   height: 50px;
//   line-height: 50px;
//   border-radius: 0px !important;
//   .hamburger-container {
//     line-height: 58px;
//     height: 50px;
//     float: left;
//     padding: 0 10px;
//   }
//   .breadcrumb-container{
//     float: left;
//   }
//   .errLog-container {
//     display: inline-block;
//     vertical-align: top;
//   }
//   .right-menu {
//     float: right;
//     height: 100%;
//     &:focus{
//      outline: none;
//     }
//     .right-menu-item {
//       display: inline-block;
//       margin: 0 8px;
//     }
//     .screenfull {
//       vertical-align: top;
//       height: 20px;
//     }
//     .international{
//       line-height: 20px;
//       vertical-align: top;
//     }
//     .theme-switch {
//       vertical-align: 15px;
//     }
//     .avatar-container {
//       height: 50px;
//       margin-right: 30px;
//       .avatar-wrapper {
//         cursor: pointer;
//         margin-top: 5px;
//         position: relative;
//         .user-avatar {
//           width: 40px;
//           height: 40px;
//           border-radius: 10px;
//         }
//         .el-icon-caret-bottom {
//           position: absolute;
//           right: -20px;
//           top: 25px;
//           font-size: 12px;
//         }
//       }
//     }
//     .connection-select {
//       height: 20px;
//     }
//     .profile-menu {
//       height: 20px;
//     }
//   }
// }
</style>
