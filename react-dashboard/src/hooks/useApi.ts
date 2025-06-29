

import { useState } from 'react';
import type {UserActivityEvent, UserActivityRequest} from "../types";

const API_BASE_URL = 'http://localhost:8080/api/events';

export const useApi = () => {
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState('');

    const clearError = () => setError('');

    const loadEvents = async (): Promise<UserActivityEvent[]> => {
        setIsLoading(true);
        setError('');
        try {
            const response = await fetch(`${API_BASE_URL}/dashboard`);
            if (!response.ok) {
                const errorData = await response.json().catch(() => ({ message: 'Unknown server error.' }));
                throw new Error(errorData.message || 'Failed to load events');
            }
            const data: UserActivityEvent[] = await response.json();
            return data.sort((a, b) => new Date(b.timestamp).getTime() - new Date(a.timestamp).getTime());
        } catch (err: any) {
            const errorMessage = err.message || 'Failed to load events. Please check server.';
            setError(errorMessage);
            console.error('Error loading events:', err);
            throw new Error(errorMessage);
        } finally {
            setIsLoading(false);
        }
    };

    const publishEvent = async (eventData: UserActivityRequest): Promise<void> => {
        setIsLoading(true);
        setError('');
        try {
            const response = await fetch(`${API_BASE_URL}/publish`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(eventData),
            });

            if (!response.ok) {
                const errorData = await response.json().catch(() => ({ message: 'Unknown server error.' }));
                throw new Error(errorData.message || 'Failed to publish event');
            }
        } catch (err: any) {
            const errorMessage = err.message || 'Failed to publish event. Please check inputs and server.';
            setError(errorMessage);
            console.error('Error publishing event:', err);
            throw new Error(errorMessage);
        } finally {
            setIsLoading(false);
        }
    };

    const searchEventsByUserId = async (userId: string): Promise<UserActivityEvent[]> => {
        setIsLoading(true);
        setError('');
        try {
            if (!/^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$/.test(userId)) {
                throw new Error('Invalid User ID format. Please enter a valid UUID.');
            }
            const response = await fetch(`${API_BASE_URL}/by-user?userId=${encodeURIComponent(userId)}`);
            if (!response.ok) {
                const errorData = await response.json().catch(() => ({ message: 'Unknown server error.' }));
                throw new Error(errorData.message || 'Failed to search events by user ID');
            }
            const data: UserActivityEvent[] = await response.json();
            return data.sort((a, b) => new Date(b.timestamp).getTime() - new Date(a.timestamp).getTime());
        } catch (err: any) {
            const errorMessage = err.message || 'Failed to search events by user ID. Please check User ID and server.';
            setError(errorMessage);
            console.error('Error searching events by user ID:', err);
            throw new Error(errorMessage);
        } finally {
            setIsLoading(false);
        }
    };

    return { isLoading, error, clearError, loadEvents, publishEvent, searchEventsByUserId };
};
