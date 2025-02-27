import {
  Category,
  MonitoringLocation,
  NodeQuerySnmpParams,
  SetOperator
} from '@/types'
import { isIP } from 'is-ip'

/** Parse node label from a vue-router route.query object */
export const parseNodeLabel = (queryObject: any) => {
  return queryObject.nodename as string || queryObject.nodeLabel as string || ''
}

/**
 * Parse categories from a vue-router route.query object.
 * The route.query 'categories' string can be a comma- or semicolon-separated list of either
 * numeric Category ids or names.
 * comma: Union; semicolon: Intersection
 * 
 * @returns The category mode and categories parsed from the queryObject. If 'selectedCategories' is empty,
 * it means no categories were present.
 */
export const parseCategories = (queryObject: any, categories: Category[]) => {
  let categoryMode: SetOperator = SetOperator.Union
  const selectedCategories: Category[] = []

  const queryCategories = queryObject.categories as string ?? ''

  if (selectedCategories.length > 0) {
    categoryMode = queryCategories.includes(';') ? SetOperator.Intersection : SetOperator.Union

    const cats: string[] = queryCategories.replace(';', ',').split(',')

    // add any valid categories
    cats.forEach(c => {
      if (/\d+/.test(c)) {
        // category id number
        const id = parseInt(c)

        const item = categories.find(x => x.id === id)

        if (item) {
          selectedCategories.push(item)
        }
      } else {
        // category name, case insensitive
        const item = categories.find(x => x.name.toLowerCase() === c.toLowerCase())

        if (item) {
          selectedCategories.push(item)
        }
      }
    })
  }

  return {
    categoryMode,
    selectedCategories
  }
}

export const parseMonitoringLocation = (queryObject: any, monitoringLocations: MonitoringLocation[]) => {
  const locationName = queryObject.monitoringLocation as string || ''

  if (locationName) {
    return monitoringLocations.find(x => x.name.toLowerCase() === locationName.toLowerCase()) ?? null
  }

  return null
}

export const parseFlows = (queryObject: any) => {
  const flows = (queryObject.flows as string || '').toLowerCase()

  if (flows === 'true') {
    return ['Ingress', 'Egress']
  } else if (flows === 'ingress') {
    return ['Ingress']
  } else if (flows === 'egress') {
    return ['Egress']
  }

  // TODO: we don't yet have support for excluding flows, i.e. if queryObject.flows === 'false'

  return []
}

/**
 * Currently this accepts anything in any valid IPv4 or IPv6 format (see `is-ip`), but
 * some formats may not actually be supported by our FIQL search.
 */
export const parseIplike = (queryObject: any) => {
  const ip = queryObject.iplike as string || queryObject.ipAddress as string || ''

  if (ip && isIP(ip)) {
    return ip
  }

  return null
}

export const parseSnmpParams = (queryObject: any) => {
  const snmpIfAlias = queryObject.snmpifalias as string || ''
  const snmpIfDesc = queryObject.snmpifdesc as string || ''
  const snmpIfIndex = queryObject.snmpifindex as string || ''
  const snmpIfName = queryObject.snmpifname as string || ''

  if (snmpIfAlias || snmpIfDesc || snmpIfIndex || snmpIfName) {
    return {
      snmpIfAlias,
      snmpIfDesc,
      snmpIfIndex,
      snmpIfName
    } as NodeQuerySnmpParams
  }

  return null
}
