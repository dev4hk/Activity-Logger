import {ACTIVITY_TYPES, type UserActivityEvent, type UserActivityRequest} from "./types";
import {useCallback, useEffect, useState} from "react";
import {useWebSocket} from "./hooks/useWebSocket.ts";
import {useApi} from "./hooks/useApi.ts";
import StatusIndicator from "./components/StatusIndicator.tsx";
import EventForm from "./components/EventForm.tsx";
import PublishControls from "./components/PublishControls.tsx";
import EventsList from "./components/EventsList.tsx";
import StatisticsCards from "./components/StatisticsCards.tsx";

function App() {
    const [events, setEvents] = useState<UserActivityEvent[]>([]);
    const [filterUserId, setFilterUserId] = useState<string>('');
    const [successMessage, setSuccessMessage] = useState<string>('');
    const [formData, setFormData] = useState<UserActivityRequest>({
        userId: '',
        activityType: ACTIVITY_TYPES[0],
        details: ''
    });


    const { isConnected, connect, disconnect } = useWebSocket();
    const { isLoading, error, clearError, loadEvents, publishEvent } = useApi();


    const handleNewEvent = useCallback((newEvent: UserActivityEvent) => {
        setEvents(prev => {
            if (prev.some(event => event.id === newEvent.id)) {
                return prev;
            }
            return [newEvent, ...prev].slice(0, 50);
        });
    }, []);


    const handleLoadEvents = async () => {
        try {
            const loadedEvents = await loadEvents();
            setEvents(loadedEvents);
        } catch (err) {
            console.error("Error in handleLoadEvents:", err);
        }
    };


    const handlePublishEvent = async () => {
        try {
            await publishEvent(formData);
            setSuccessMessage('Event published successfully!');

            setFormData({ userId: '', activityType: ACTIVITY_TYPES[0], details: '' });
            setTimeout(() => setSuccessMessage(''), 3000);
        } catch (err) {
            console.error("Error in handlePublishEvent:", err);
        }
    };


    const handlePublishRandomEvent = async (eventData: UserActivityRequest) => {
        try {
            await publishEvent(eventData);
            setSuccessMessage('Random event published!');
            setTimeout(() => setSuccessMessage(''), 3000);
        } catch (err) {
            console.error("Error in handlePublishRandomEvent:", err);
        }
    };


    useEffect(() => {
        connect(handleNewEvent);
        handleLoadEvents();
        return () => {
            disconnect();
        };
    }, [connect, disconnect, handleNewEvent]);


    return (
        <div className="min-h-screen bg-gray-100 dark:bg-gray-900 text-gray-900 dark:text-gray-100 p-4">
            <div className="max-w-6xl mx-auto py-8">
                <header className="mb-8 text-center">
                    <h1 className="text-4xl font-extrabold text-gray-900 dark:text-gray-100 mb-4">
                        User Activity Dashboard
                    </h1>
                    <StatusIndicator isConnected={isConnected} />
                </header>

                <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
                    <div className="lg:col-span-1 space-y-6">
                        <EventForm
                            formData={formData}
                            onFormChange={setFormData}
                            onSubmit={handlePublishEvent}
                            isLoading={isLoading}
                            error={error}
                            onErrorDismiss={clearError}
                            successMessage={successMessage}
                        />
                        <PublishControls
                            onPublishRandom={handlePublishRandomEvent}
                            onRefreshAll={handleLoadEvents}
                        />
                    </div>

                    <div className="lg:col-span-2 space-y-6">
                        <EventsList
                            events={events}
                            filterUserId={filterUserId}
                            onFilterChange={setFilterUserId}
                            isLoading={isLoading}
                            onRefresh={handleLoadEvents}
                        />

                        {/* Statistics Cards */}
                        <StatisticsCards events={events} />
                    </div>
                </div>
            </div>
        </div>
  )
}

export default App
