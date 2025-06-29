import { useState, useRef, useCallback } from 'react';
import SockJS from 'sockjs-client';
import type {UserActivityEvent} from "../types";
import {Client, type IFrame, type IMessage} from "@stomp/stompjs";

const WS_URL = 'http://localhost:8080/ws-activities';


export const useWebSocket = () => {
    const [isConnected, setIsConnected] = useState(false);
    const stompClient = useRef<Client | null>(null);

    const connect = useCallback((onMessage: (event: UserActivityEvent) => void) => {

        if (stompClient.current && stompClient.current.connected) {
            console.log('useWebSocket: Already connected.');
            setIsConnected(true);
            return;
        }

        console.log('useWebSocket: Attempting to connect to WebSocket...');
        const socket = new SockJS(WS_URL);

        stompClient.current = new Client({
            webSocketFactory: () => socket,
            debug: (str) => {

            },
            onConnect: (frame: IFrame) => {
                console.log('useWebSocket: Connected to WebSocket', frame);
                setIsConnected(true);

                stompClient.current?.subscribe('/topic/activities', (message: IMessage) => {
                    try {
                        const newEvent: UserActivityEvent = JSON.parse(message.body);
                        onMessage(newEvent);
                    } catch (error) {
                        console.error('useWebSocket: Error parsing WebSocket message:', error);
                    }
                });
            },
            onDisconnect: () => {
                console.log('useWebSocket: Disconnected from WebSocket');
                setIsConnected(false);
            },
            onStompError: (frame: IFrame) => {
                console.error('useWebSocket: Broker reported STOMP error:', frame.headers['message']);
                console.error('useWebSocket: Details:', frame.body);
                setIsConnected(false);
            },
            onWebSocketClose: (event: CloseEvent) => {
                console.error('useWebSocket: WebSocket closed:', event);
                setIsConnected(false);
            }
        });

        stompClient.current.activate();
    }, []);

    const disconnect = useCallback(() => {
        if (stompClient.current && stompClient.current.connected) {
            stompClient.current.deactivate();
        }
    }, []);

    return { isConnected, connect, disconnect };
};
