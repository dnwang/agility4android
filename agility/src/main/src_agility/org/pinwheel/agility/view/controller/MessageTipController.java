package org.pinwheel.agility.view.controller;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Copyright (C), 2016 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 * @version 8/24/16,23:02
 * @see
 */
public final class MessageTipController {

    private static final class Node {
        Node parent;
        IMessageTreeNode self;
        Set<Node> child;
    }

    private ViewGroup rootView;
    private Map<String, Node> nodes;

    public MessageTipController(Activity activity) {
        this((ViewGroup) activity.getWindow().getDecorView());
    }

    public MessageTipController(ViewGroup root) {
        if (null == root) {
            throw new NullPointerException("MessageTipController root viewGroup is not empty.");
        }
        nodes = new HashMap<>();
        rootView = root;
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                restructure();
            }
        });
    }

    private void restructure() {
        if (!nodes.isEmpty()) {
            nodes.clear();
        }
        findAndBind(rootView, nodes);
        for (Node node1 : nodes.values()) {
            for (Node node2 : nodes.values()) {
                if (node1 == node2) {
                    continue;
                }
                if (node1.self.getNodeName().equals(node2.self.getParentNodeName())) {
                    if (null == node1.child) {
                        node1.child = new HashSet<>();
                    }
                    node2.parent = node1;
                    node1.child.add(node2);
                } else if (node2.self.getNodeName().equals(node1.self.getParentNodeName())) {
                    if (null == node2.child) {
                        node2.child = new HashSet<>();
                    }
                    node1.parent = node2;
                    node2.child.add(node1);
                }
            }
        }
        update();
    }

    private void findAndBind(ViewGroup root, Map<String, Node> nodes) {
        final int size = root.getChildCount();
        for (int i = 0; i < size; i++) {
            View view = root.getChildAt(i);
            if (view instanceof ViewGroup) {
                findAndBind((ViewGroup) view, nodes);
            } else if (view instanceof IMessageTreeNode) {
                IMessageTreeNode treeNode = (IMessageTreeNode) view;
                String nodeName = treeNode.getNodeName();
                if (TextUtils.isEmpty(nodeName)) {
                    continue;
                }
                Node node = new Node();
                node.self = treeNode;
                nodes.put(nodeName, node);
                treeNode.onBind(this);
            }
        }
    }

    public void update() {
        for (Node node : nodes.values()) {
            if (null == node.child) {
                update(node);
            }
        }
    }

    public void update(IMessageTreeNode source) {
        if (null == source) {
            return;
        }
        String nodeName = source.getNodeName();
        if (TextUtils.isEmpty(nodeName)) {
            return;
        }
        if (!nodes.containsKey(nodeName)) {
            return;
        }
        update(nodes.get(nodeName));
    }

    /**
     * 节点信息更新
     */
    private void update(Node node) {
        final Node root = findRoot(node);
        // 已节点为界,向上更新所有父节点
        if (root != node) {
            root.self.onMessageCountChanged(getSizeEndWith(root, node));
        }
        // 从自己开始,向下设置所有子节点
        // 目前仅当count<=0时清空
        if (node.self.getMessageCount() <= 0) {
            clearStartWith(node);
        }
    }

    private int getSizeEndWith(Node begin, Node end) {
        if (begin == end) {
            return end.self.getMessageCount();
        }
        int size = 0;
        if (null != begin.child) {
            for (Node n : begin.child) {
                int childSize = getSizeEndWith(n, end);
                if (null != n.child) {
                    n.self.onMessageCountChanged(childSize);
                }
                size += childSize;
            }
        } else {
            size = begin.self.getMessageCount();
        }
        return size;
    }

    private void clearStartWith(Node begin) {
        begin.self.onMessageCountChanged(0);
        if (null != begin.child) {
            for (Node n : begin.child) {
                clearStartWith(n);
            }
        }
    }

    private Node findRoot(Node node) {
        if (null == node.parent) {
            return node;
        } else {
            return findRoot(node.parent);
        }
    }

    public interface IMessageTreeNode {
        void onBind(MessageTipController controller);

        void onMessageCountChanged(int count);

        String getNodeName();

        String getParentNodeName();

        int getMessageCount();

        void setParentNodeName(String name);

        void setNodeName(String name);
    }
}
