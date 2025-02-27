<template>
  <div class="editor">
    <div class="toolbar">
      <FeatherButton
        v-if="reverseLog"
        :disabled="!selectedLog"
        class="btn"
        icon="Display oldest first."
        @click="getLog(false)"
      >
        <FeatherIcon :icon="KeyboardArrowDown" />
      </FeatherButton>

      <FeatherButton
        v-if="!reverseLog"
        :disabled="!selectedLog"
        class="btn"
        icon="Display newest first."
        @click="getLog(true)"
      >
        <FeatherIcon :icon="KeyboardArrowUp" />
      </FeatherButton>
    </div>
    <VAceEditor
      v-model:value="content"
      lang="text"
      :theme="theme"
      style="height: 100%"
      :printMargin="false"
      @init="init"
    />
  </div>
</template>

<script setup lang="ts">
import { VAceEditor } from 'vue3-ace-editor'
import { FeatherIcon } from '@featherds/icon'
import { FeatherButton } from '@featherds/button'
import { onKeyStroke } from '@vueuse/core'
import KeyboardArrowUp from '@featherds/icon/hardware/KeyboardArrowUp'
import KeyboardArrowDown from '@featherds/icon/hardware/KeyboardArrowDown'
import ace from 'ace-builds'
import 'ace-builds/src-noconflict/mode-text'
import 'ace-builds/src-noconflict/theme-xcode'
import 'ace-builds/src-noconflict/theme-dracula'
import 'ace-builds/src-noconflict/ext-searchbox'
import { useAppStore } from '@/stores/appStore'
import { useLogStore } from '@/stores/logStore'

const appStore = useAppStore()
const logStore = useLogStore()
const reverseLog = ref(false)
const content = ref('')
const logString = computed(() => logStore.log)
const selectedLog = computed(() => logStore.selectedLog)
const editorRef = ref()

const theme = computed(() => {
  const theme = appStore.theme
  if (theme === 'open-dark') {
    return 'dracula'
  }

  return 'xcode'
})

onKeyStroke('f', (e) => {
  if (e.ctrlKey || e.metaKey) {
    e.preventDefault()
    editorRef.value.searchBox.show()
  }
})

const getLog = (reverse: boolean) => {
  logStore.setReverseLog(reverse)
  logStore.getLog(selectedLog.value)
  reverseLog.value = reverse
}

watchEffect(() => content.value = logString.value)
const init = (editor: any) => {
  // activate and hide seach box
  ace.config.loadModule('ace/ext/searchbox', (m) => m.Search(editor))
  editor.searchBox.hide()

  editor.setFontSize(15)
  editor.setOptions({ readOnly: true })
  editor.renderer.setShowGutter(false)
  editor.renderer.$cursorLayer.element.style.display = 'none'

  editorRef.value = editor
}
</script>

<style lang="scss" scoped>
@import "@featherds/styles/themes/variables";
.editor {
  height: calc(100vh - 120px);
  display: flex;
  flex-direction: column;
  border: 1px solid var($border-on-surface);

  .toolbar {
    display: block;
    width: 100%;
    height: 30px;
    background: var($shade-3);

    .btn {
      margin: 0px;
      float: right;
      height: 25px !important;
      width: 25px !important;
      min-width: 25px !important;
      margin-right: 5px;
      margin-top: 2px;
      svg {
        font-size: 20px !important;
      }
    }
  }
}
</style>
