import React from 'react';

interface StatusIndicatorProps {
    isConnected: boolean;
}

const StatusIndicator: React.FC<StatusIndicatorProps> = ({ isConnected }) => (
    <div className={`flex items-center space-x-2 ${isConnected ? 'text-green-600' : 'text-red-600'} dark:text-gray-300`}>
        <div className={`w-3 h-3 rounded-full ${isConnected ? 'bg-green-500' : 'bg-red-500'}`}></div>
        <span className="text-sm font-medium">
            WebSocket: {isConnected ? 'Connected' : 'Disconnected'}
        </span>
    </div>
);

export default StatusIndicator;
