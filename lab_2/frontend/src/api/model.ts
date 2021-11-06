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

export interface Language {
  natural: string,
  code: string
}

export interface AccessLog {
  server: string,
  httpVersion: string,
  authority: string,
  uri: string,
  method: string,
  requestContentType: string,
  requestBodySize: number,
  responseStatus: number,
  responseContentType: string,
  responseBodySize: number,
  userLogin: string,
  id: string,
  timestamp: Date
}
