export interface ObjectStorage {
  name: string
}

export interface ObjectItem {
  key: string
}

export interface VirtualMachine {
  instanceId: string,
  stateName: string
}

export interface ImageLabel {
  name: string,
  confidence: number
}

export interface ImageText {
  content: string,
  confidence: number
}
