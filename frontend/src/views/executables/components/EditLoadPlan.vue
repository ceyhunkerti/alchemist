<template lang="pug">
el-dialog(
  @close="onClose"
  :visible="visible"
  :title="plan.name"
  fullscreen
  @open="onShow"
)
  ul.contextmenu(v-show='contextMenu.visible' :style="{left:contextMenu.left+'px',top:contextMenu.top+'px'}")
    li Duplicate Selection
    li Rename
    li Remove
  .components-container
    .toolbar
    split-pane(v-on:resize='onResize' split='vertical' :default-percent='25')
      template(slot='paneL')
        .left-container(v-loading="loadingScenarios")
          .search-container
            el-input(placeholder="Search ...")
          .scen-container
            .list-complete-item(
              v-for='element in scenarios'
              v-on:dragend="(e) => onDragEnd(e, element)"
              v-on:dragstart="(e) => onDragStart(e, element)"
              :key='element.internalId'
              draggable="true"
            )
              .list-complete-item-handle {{element.name}}
      template(slot='paneR')
        .right-container
          .toolbar
            .clearfix
              el-button(@click="onDelete" type="danger" plain title="Delete" icon='el-icon-delete' circle style="float:right;margin-left:10px;")
              el-dropdown(style="float:right;margin-left:10px;" @command="onAddStepMenu" trigger="click")
                el-button(:disabled="loading" type="success" plain icon="el-icon-plus" title="Add step" circle)
                el-dropdown-menu(slot='dropdown')
                  el-dropdown-item(command="PARALLEL") Parallel Step
                  el-dropdown-item(command="SERIAL") Serial Step
              el-button(:disabled="loading" type="primary" plain @click="onSave" title="Save" circle style="float:right;margin-left:10px;")
                svg-icon(icon-class='save')
              el-button(:disabled='!selected' @click="onRename" type="warning" title="Rename" icon="el-icon-edit" circle plain style="float:right;margin-left:10px;")
              el-button(:disabled="loading" @click="onDown" icon="el-icon-arrow-down" title="Move down" circle style="float:right;margin-left:10px;")
              el-button(:disabled="loading" @click="onUp" icon="el-icon-arrow-up" title="Move up" circle style="float:right;margin-left:10px;")
              el-input(v-model="filterText" prefix-icon="el-icon-search" placeholder="Search ..." style="width:400px;float:right")
          el-tree(
            style="margin-bottom:100px;margin-top:10px;"
            v-loading="loading"
            ref="plan"
            :data='steps'
            node-key='stepId'
            :highlight-current="true"
            :expand-on-click-node='false'
            :props="treeProps"
            :render-content="renderContent"
            :filter-node-method="filterNode"
            @node-drop="handleDrop"
            @current-change="(current) => { this.selected = current }"
            :allow-drop="allowDrop",
            draggable
          )

</template>

<script>
import { Message } from 'element-ui'
import uuidv1 from 'uuid/v1'
import _ from 'lodash'
import splitPane from 'vue-splitpane'
import { mapActions, mapGetters } from 'vuex'

const findParent = ({ stepId }, node, parent) => {
  parent = parent || node

  const index = _.findIndex(parent.children, c => {
    return (stepId && stepId === c.stepId)
  })
  if (index > -1) {
    return { parent, index }
  } else {
    let result = {}
    _.some(node.children, d => {
      result = findParent({ stepId }, d, node)
      return !_.isEmpty(result)
    })
    return result
  }
}

// const swap = (arr, index1, index2) => {
//   const t1 = arr[index1]
//   const t2 = arr[index2]
//   arr[index1] = t2
//   arr[index2] = t1
//   return arr
// }
export default {
  components: { splitPane },
  props: {
    visible: { type: Boolean, default: false },
    plan: { type: Object, default: () => {} }
  },
  data() {
    return {
      filterText: '',
      loadingScenarios: false,
      scenarios: [],
      treeProps: {
        label: 'name'
      },
      loading: false,
      drag: {
        source: undefined,
        target: undefined
      },
      p: {},
      selected: false,
      contextMenu: {
        visible: false,
        data: undefined,
        left: 0,
        top: 0
      }
    }
  },
  watch: {
    filterText(v) {
      this.$refs.plan.filter(v)
    },
    'contextMenu.visible'(value) {
      if (value) {
        document.body.addEventListener('click', this.onCloseMenu)
      } else {
        document.body.removeEventListener('click', this.onCloseMenu)
      }
    }
  },
  computed: {
    ...mapGetters(['connection']),
    steps() {
      if (!this.p) { return [] }
      if (this.p.rootStep) {
        return [this.p.rootStep]
      }
      return []
    }
  },
  methods: {
    ...mapActions('executable', [
      'findScenarios'
    ]),
    onCloseMenu() {
      this.contextMenu.visible = false
    },
    onRename() {
      const current = this.$refs.plan.getCurrentNode()
      this.$prompt('', 'Raname', {
        confirmButtonText: 'OK',
        cancelButtonText: 'Cancel',
        inputValue: current.name,
        inputValidator: (v) => {
          return v ? true : 'Can not be blank'
        }
      }).then(e => {
        if (!e.value) {
          Message.warning('Can not be blank!')
          return
        }
        current.name = e.value
      })
    },
    onAddStepMenu(command) {
      const tree = this.$refs.plan
      const name = _.capitalize(command)
      const stepType = command
      const stepId = uuidv1()
      const children = []
      const node = { name, stepType, stepId, children }
      const current = tree.getCurrentNode()
      if (current.children) {
        const last = _.last(current.children)
        tree.insertAfter(node, last)
      } else {
        tree.insertAfter(node, current)
      }
    },
    onUp() {
      const tree = this.$refs.plan
      const current = tree.getCurrentNode()
      const { index, parent } = findParent(current, this.p.rootStep)
      if (index <= 0) { return }
      const sibling = tree.getNode(parent.children[index - 1].stepId)
      const node = _.clone(current)
      delete node.$treeNodeId
      node.stepId = uuidv1()
      console.log(tree.remove(current.stepId))
      tree.insertBefore(node, sibling)
      tree.setCurrentNode(node)
    },
    onDown() {
      const tree = this.$refs.plan
      const current = tree.getCurrentNode()
      const { index, parent } = findParent(current, this.p.rootStep)
      if (index === parent.children.length - 1) { return }
      const sibling = tree.getNode(parent.children[index + 1].stepId)
      const node = _.clone(current)
      delete node.$treeNodeId
      node.stepId = uuidv1()
      tree.remove(current)
      tree.insertAfter(node, sibling)
      tree.setCurrentNode(node)
    },
    onDelete() {
      const tree = this.$refs.plan
      const current = tree.getCurrentNode()
      tree.remove(current)
    },
    onSave() {},
    allowDrop(draggingNode, dropNode, type) {
      // return false
      const isContainer = dropNode.data.children !== undefined
      if (!isContainer) { return false }
      // const current = draggingNode.data
      const { parent } = findParent(dropNode.data, this.p.rootStep)
      const isRoot = (!parent || (parent.stepId === dropNode.data.stepId))
      if (isRoot && ['next', 'prev'].indexOf(type) !== -1) { return false }
      return true
    },
    onDragStart(event, element) {
      this.drag.source = element
      event.target.style.backgroundColor = '#FFF3E0'
    },
    onDragEnd(e) {
      this.drag.source = undefined
      e.target.style.backgroundColor = ''
    },
    onResize() {},
    onClose() { this.$emit('close') },
    onShow() { this.init() },
    loadScenarios() {
      if (!this.connection) {
        return
      }
      const connectionId = this.connection.id
      this.loadingScenarios = true
      this.findScenarios({ connectionId }).then(response => {
        this.loadingScenarios = false
        this.scenarios = response.data
      },
      error => {
        this.loadingScenarios = false
        console.log(error)
      })
    },
    filterNode(value, data) {
      if (!value) return true
      return data.name.toUpperCase().indexOf(value.toUpperCase()) !== -1
    },
    renderContent(h, { node, data, store }) {
      const getStepType = (s) => {
        if (!s) return
        return s[0].toUpperCase()
      }

      const getContainerStyle = (s) => {
        return `margin-right:10px;font-size:11px;font-weight:bold;color:#42A5F5`
      }

      const clearStyle = (e) => {
        e.target.parentElement.parentElement.parentElement.style.backgroundColor = ''
        e.target.parentElement.parentElement.parentElement.style.borderBottom = ''
      }

      const onDragEnter = (e) => {
        e.target.parentElement.parentElement.parentElement.style.backgroundColor = '#b9f6ca'
        e.target.parentElement.parentElement.parentElement.style.borderBottom = 'dashed 1px black'
      }

      const onDragLeave = (e) => {
        clearStyle(e)
      }

      const onDrop = (e, target) => {
        console.log('drop')
        clearStyle(e)
        const findNode = (node, parent) => {
          if (node.stepId === target.stepId) {
            const n = node
            parent = parent || n
            return { n, parent }
          } else {
            let result = {}
            _.some(node.children, d => {
              result = findNode(d, node)
              return (result.n || result.parent)
            })
            return result
          }
        }
        const root = this.p.rootStep
        const { n, parent } = findNode(root)
        this.drag.source.stepId = uuidv1()

        if (!n.children) {
          const index = _.findIndex(parent.children, { stepId: n.stepId })
          parent.children.splice(index, 0, _.cloneDeep(this.drag.source))
        } else {
          n.children.splice(0, 0, _.cloneDeep(this.drag.source))
        }
      }
      const onContextMenu = (e, d) => {
        e.preventDefault()
        this.contextMenu.visible = true
        this.contextMenu.data = d
        this.contextMenu.left = e.clientX
        this.contextMenu.top = e.clientY
      }
      return (
        <span class='custom-tree-node'
          on-drop={ (e) => onDrop(e, data) }
          on-dragenter={ (e) => onDragEnter(e) }
          on-dragleave={ (e) => onDragLeave(e) }
          on-contextmenu={ (e) => onContextMenu(e, data) }
        >
          <span>
            <span
              title={data.stepType}
              style={getContainerStyle(data.stepType)}>{getStepType(data.stepType)}</span>
            <span>{data.name}</span>
          </span>
          <span>
            <el-tag size='mini' type='danger' style={ (data.isEnabled || data.isEnabled === undefined) ? 'display:none;' : '' }>
              DISABLED
            </el-tag>
          </span>
        </span>
      )
    },
    handleDrop(draggingNode, dropNode, dropType, ev) {
      ev.preventDefault()
    },
    init() {
      this.selected = false
      this.p = _.cloneDeep(this.plan)
      this.loadScenarios()
    }
  }
}
</script>

<style >
.components-container {
  position: relative;
  height: 100vh;
}

.left-container {
  /* background-color: #F38181; */
  height: 100%;
}

.right-container {
  /* background-color: #FCE38A; */
  width: 100%;
  height: 100%;
}

.search-container {
  padding: 10px;
  border-bottom: solid 1px rgb(236, 230, 230);
}

.scen-container {
  padding: 10px;
}



</style>

<style rel="stylesheet/scss" lang="scss" scoped>

.el-dialog__body {
  padding: 0 !important;
}

.contextmenu {
  margin: 0;
  background: #fff;
  z-index: 100;
  position: absolute;
  list-style-type: none;
  padding: 5px 0;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 400;
  color: #333;
  box-shadow: 2px 2px 3px 0 rgba(0, 0, 0, .3);
  li {
    margin: 0;
    padding: 7px 16px;
    cursor: pointer;
    &:hover {
      background: #eee;
    }
  }
}

.dndList {
  background: #fff;
  padding-bottom: 40px;
  &:after {
    content: "";
    display: table;
    clear: both;
  }
  .dndList-list {
    float: left;
    padding-bottom: 30px;
    &:first-of-type {
      margin-right: 2%;
    }
    .dragArea {
      margin-top: 15px;
      min-height: 50px;
      padding-bottom: 30px;
    }
  }
}

.list-complete-item {
  cursor: pointer;
  position: relative;
  font-size: 14px;
  padding: 5px 12px;
  margin-top: 4px;
  border: 1px solid #ebeef5;
  border-radius: 3px;
  transition: all 1s;
}

.list-complete-item:hover {
  box-shadow: 0 2px 12px 0 rgba(0,0,0,.1);
}


.list-complete-item-handle {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-right: 50px;
}

.list-complete-item-handle2 {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-right: 20px;
}

.list-complete-item.sortable-chosen {
  background: #4AB7BD;
}

.list-complete-item.sortable-ghost {
  background: #30B08F;
}

.list-complete-enter,
.list-complete-leave-active {
  opacity: 0;
}
</style>
