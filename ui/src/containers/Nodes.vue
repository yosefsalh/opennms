<template>
  <div class="feather-row">
    <div class="feather-col-12">
      <BreadCrumbs :items="breadcrumbs" />
    </div>
  </div>
  <div class="feather-row">
    <div class="feather-col-12">
      <div class="card">
        <div class="feather-row">
          <div class="feather-col-2">
            <NodeStructurePanel />
          </div>
          <div :class="`feather-col-10`">
            <NodesTable />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
  
<script setup lang="ts">
import { useRoute, useRouter } from 'vue-router'
import NodesTable from '@/components/Nodes/NodesTable.vue'
import NodeStructurePanel from '@/components/Nodes/NodeStructurePanel.vue'
import BreadCrumbs from '@/components/Layout/BreadCrumbs.vue'
import { useNodeQuery } from '@/components/Nodes/hooks/useNodeQuery'
import { loadNodePreferences } from '@/services/localStorageService'
import { useMenuStore } from '@/stores/menuStore'
import { useNodeStructureStore } from '@/stores/nodeStructureStore'
import { BreadCrumb, NodePreferences } from '@/types'

const menuStore = useMenuStore()
const nodeStructureStore = useNodeStructureStore()
const { buildNodeQueryFilterFromQueryString, queryStringHasTrackedValues } = useNodeQuery()

const route = useRoute()
const router = useRouter()
const homeUrl = computed<string>(() => menuStore.mainMenu?.homeUrl)

const breadcrumbs = computed<BreadCrumb[]>(() => {
  return [
    { label: 'Home', to: homeUrl.value, isAbsoluteLink: true },
    { label: 'Nodes', to: '#', position: 'last' }
  ]
})

onMounted(() => {
  // load any saved preferences
  const prefs = loadNodePreferences()

  if (queryStringHasTrackedValues(route.query)) {
    const nodeFilter = buildNodeQueryFilterFromQueryString(route.query, nodeStructureStore.categories, nodeStructureStore.monitoringLocations)

    const newPrefs = {
      nodeColumns: prefs?.nodeColumns || [],
      nodeFilter
    } as NodePreferences

    nodeStructureStore.setFromNodePreferences(newPrefs)

    // TODO: Save prefs???
    router.replace({ name: 'Nodes' })
    return
  }

  if (prefs) {
    nodeStructureStore.setFromNodePreferences(prefs)
  }
})
</script>
  
<style lang="scss" scoped>
@import "@featherds/styles/themes/variables";

</style>
