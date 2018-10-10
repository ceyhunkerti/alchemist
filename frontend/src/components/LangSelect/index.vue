<template lang="pug">
el-dropdown.international(trigger='click' @command='handleSetLanguage')
  div
    svg-icon(class-name='international-icon' icon-class='language')
  el-dropdown-menu.menu(slot='dropdown')
    el-dropdown-item(command='en' :disabled="language==='en'") English
    el-dropdown-item(command='tr' :disabled="language==='tr'") Turkish
</template>

<script>
export default {
  computed: {
    language() {
      return this.$store.getters.language
    }
  },
  methods: {
    handleSetLanguage(lang) {
      this.$i18n.locale = lang
      this.$store.dispatch('setLanguage', lang)
      this.$message({
        message: 'switch language success',
        type: 'success'
      })
    }
  }
}
</script>

<style scoped>
.international-icon {
  font-size: 20px;
  cursor: pointer;
  line-height: 20px !important;
  /* vertical-align: -5px!important; */
}
.el-dropdown-selfdefine {
  line-height: unset !important;
}
</style>


