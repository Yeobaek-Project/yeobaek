package com.ssafy.yeobaeg.presentation.arcore.common

import io.github.sceneview.node.Node
import io.github.sceneview.ar.node.AnchorNode

/** Scene 노드 안전 제거 */
fun removeNodeSafe(node: Node, bag: MutableList<Node>) {
    runCatching {
        node.parent?.removeChildNode(node)
        bag.remove(node)
    }
}

/** Anchor 해제 안전 호출 */
fun detachAnchorSafe(anchorNode: AnchorNode) {
    runCatching { anchorNode.anchor.detach() }
}
